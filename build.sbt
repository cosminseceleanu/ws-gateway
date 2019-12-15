name := """gateway"""
organization := "com.cosmin"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.1"

lazy val root = (project in file("."))

lazy val application = (project in file("application"))