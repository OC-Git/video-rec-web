package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.Configuration;
import play.Logger;
import sun.misc.BASE64Encoder;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;

public class S3 {

	static public class Access {
		public String secret;
		public String key;
		public String policy;
		public String signature;
		public String bucket;
		public String website;

		@Override
		public String toString() {
			return "Access [secret=" + secret + ", key=" + key + ", policy="
					+ policy + ", signature=" + signature + ", bucket="
					+ bucket + ", website=" + website + "]";
		}
	}

	private static Access access;

	public static Access s3Access() throws Exception {
		if (access != null)
			return access;

		access = new Access();

		access.secret = Configuration.root().getString("aws.secret");
		access.key = Configuration.root().getString("aws.key");
		access.bucket = Configuration.root().getString("aws.s3.bucket");
		access.website = Configuration.root().getString("aws.s3.website");
		access.policy = encode(createPolicy(access).getBytes("UTF-8"));

		Mac hmac = Mac.getInstance("HmacSHA1");
		hmac.init(new SecretKeySpec(access.secret.getBytes("UTF-8"), "HmacSHA1"));
		access.signature = encode(hmac.doFinal(access.policy.getBytes("UTF-8")));

		return access;
	}

	private static String encode(byte[] input) {
		return (new BASE64Encoder()).encode(input).replaceAll("\n", "")
				.replaceAll("\r", "");
	}

	// {
	// "expiration": "2013-01-01T00:00:00Z",
	// "conditions": [
	// {"bucket": "wertuts"},
	// ["starts-with", "$key", "video/"],
	// {"acl": "public-read"},
	// {"success_action_redirect": "http://localhost:9000/offer/s3success"},
	// ["content-length-range", 0, 100000000]
	// ]
	// }
	private static String createPolicy(Access access)
			throws JsonGenerationException, JsonMappingException, IOException {
		ArrayList<Object> conditions = new ArrayList<Object>();
		conditions.add(createSimpleObject("bucket", access.bucket));
		conditions.add(createArray("starts-with", "$key", ""));
		conditions.add(createArray("starts-with", "$name", ""));
		conditions.add(createArray("starts-with", "$Filename", ""));
		conditions
				.add(createArray("starts-with", "$success_action_status", ""));
		conditions.add(createSimpleObject("acl", "public-read"));
		conditions.add(createArray("starts-with", "$content-type", ""));
		conditions.add(createArray("content-length-range", 0, 100000000));

		LinkedHashMap<String, Object> value = new LinkedHashMap<String, Object>();
		value.put("expiration", "2014-01-01T00:00:00Z");
		value.put("conditions", conditions);
		ObjectMapper mapper = new ObjectMapper();
		String policy = mapper.writer().writeValueAsString(value);
		return policy.toString().replaceAll("\n", "").replaceAll("\r", "");
	}

	private static LinkedHashMap<String, Object> createSimpleObject(
			String name, String value) {
		LinkedHashMap<String, Object> object = new LinkedHashMap<String, Object>();
		object.put(name, value);
		return object;
	}

	private static ArrayList<Object> createArray(Object... o) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (Object value : o) {
			result.add(value);
		}
		return result;
	}

	public static void delete(String key) {
		AWSCredentials awsCredentials = new BasicAWSCredentials(access.key,
				access.secret);
		AmazonS3Client client = new AmazonS3Client(awsCredentials);
		client.deleteObject(access.bucket, key);
	}

	public static InputStream open(String key) {
		AWSCredentials awsCredentials = new BasicAWSCredentials(access.key,
				access.secret);
		AmazonS3Client client = new AmazonS3Client(awsCredentials);
		S3Object object = client.getObject(access.bucket, key);
		return object.getObjectContent();
	}

	public static File download(String key) throws IOException {
		File file = File.createTempFile("s3-", ".tmp");
		Logger.debug("Download from S3:" + key + " to "
				+ file.getAbsolutePath());
		FileUtils.copyInputStreamToFile(open(key), file);
		return file;
	}

	public static void upload(String key, File file) throws Exception {
		Logger.info("Uploading to " + key);
		Access s3 = s3Access();
		AWSCredentials awsCredentials = new BasicAWSCredentials(s3.key,
				s3.secret);
		AmazonS3Client client = new AmazonS3Client(awsCredentials);
		client.putObject(s3.bucket, key, file);
	}

}
