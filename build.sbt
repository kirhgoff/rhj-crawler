name := "rhj-crawler"

version := "1.0"

scalaVersion := "2.11.2"

fork in run := true

javaOptions in run ++= Seq (
//  "-Dcom.gs.jini_lus.locators=localhost:4174",
)

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-io" % "1.3.2"
//  "com.gigaspaces" % "gs-runtime" % "10.0.0-11600-RELEASE" exclude("javax.jms", "jms"),
)

resolvers ++= Seq(
  "Moex open repo" at "http://172.20.29.16:8081/nexus/content/groups/public"
//  "jboss-3rd-party-releases" at "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/"
)


