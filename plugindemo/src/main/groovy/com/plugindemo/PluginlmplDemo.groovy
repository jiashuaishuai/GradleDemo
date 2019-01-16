package com.plugindemo

import org.gradle.api.Plugin
import org.gradle.api.Project

 public class PluginlmplDemo implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("testTask") {
            group 'userDefined'
            description 'gradle插件测试demo'
            println "hello Plugin Gradle"
        }
    }
}