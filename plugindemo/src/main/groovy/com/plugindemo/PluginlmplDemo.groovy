package com.plugindemo

import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginlmplDemo implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //利用PluginExtension创建pluginExt闭包，用于接受外部传递的参数值
        project.extensions.create('pluginExt', PluginExtension)
        project.pluginExt.extensions.create('nestExt', PluginNestExtension)
        project.task('customTask', type: CustomTask)
        //一定要加入<< 或者doLast{} 否则外部参数拿不到
        //加入队列
        project.task("testTask") << {
            group 'userDefined'
            description 'gradle插件测试demo'
            println "哈哈哈哈"+project.extensions.pluginExt.param1
        }
    }
}