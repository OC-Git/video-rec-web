import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "video-rec-web"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "postgresql" % "postgresql" % "8.4-702.jdbc4",
      "com.amazonaws" % "aws-java-sdk" % "1.3.12",
      "commons-io" % "commons-io" % "2.0.1",
      "javax.mail" % "mail" % "1.4",
      "com.github.twitter" % "bootstrap" % "2.0.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "webjars" at "http://webjars.github.com/m2"
    )

}
