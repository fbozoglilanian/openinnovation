name := "Open_Innovation"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.mindrot"  % "jbcrypt"   % "0.3m"
)     

play.Project.playScalaSettings
