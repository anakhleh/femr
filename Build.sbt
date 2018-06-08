import play.ebean.sbt.PlayEbean
import play.sbt.PlayJava
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.Import._


val appName = "femr"
val appVersion = "2.4.0-beta"
val currentScalaVersion = "2.11.7"

val appDependencies = Seq(
  // Add your project dependencies here,
  javaCore,
  javaJdbc,
  evolutions,
  jodaForms,
  guice,
  "mysql" % "mysql-connector-java" % "5.1.41",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "org.apache.commons" % "commons-text" % "1.3",
  "com.google.code.gson" % "gson" % "2.3.1",
  "com.itextpdf" % "itextpdf" % "5.5.6",
  "com.itextpdf.tool" % "xmlworker" % "5.5.6",
  "com.h2database" % "h2" % "1.4.193",
  "com.jcraft" % "jsch" % "0.1.54",
  "org.seleniumhq.selenium" % "selenium-java" % "3.9.1",
//  "org.fluentium" % "fluentlenium-junit" % "3.5.0",
  "org.fluentlenium" % "fluentlenium-core" % "3.5.2",
//  "org.fluentlenium" % "fluentlenium-adapter" % "3.5.2",
  "org.fluentlenium" % "fluentlenium-junit" % "3.5.2",
  "javax.xml.bind" % "jaxb-api" % "2.1"
//  "com.h2database" % "h2" % "1.4.197"

)
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

//initialize := {
//  val _ = initialize.value
//  if (sys.props("java.specification.version") != "1.8")
//    sys.error("Java 8 is required for this project.")
//}

val main = (project in file(".")).enablePlugins(PlayJava, PlayEbean).settings(

  javacOptions += "-Xlint:deprecation", //*/   //use when searching for deprecated API usage
  javacOptions += "-Xlint:unchecked", //*/     //use when you want to display java warnings
  version := appVersion,
  name := appName,
  scalaVersion := currentScalaVersion,
  libraryDependencies ++= appDependencies,
  pipelineStages := Seq(digest, gzip),
  testOptions in Test ~= {
    args =>
      for {
        arg <- args
        ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
        newArg = if (ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
      } yield newArg
  },
  sbt.Keys.fork in Test := false,
  doc in Compile := target.map(_ / "none").value
)

