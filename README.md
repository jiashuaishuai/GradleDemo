# GradleDemo
gradle多版本配置，
# Gradle笔记


## Project和Tasks
在grade中的两大重要的概念，分别是project和tasks。每一次构建都是有至少一个project来完成，所以Android studio中的project和Gradle中的project不是一个概念。每个project有至少一个tasks。每一个build.grade文件代表着一个project。tasks在build.gradle中定义。当初始化构建进程，gradle会基于build文件，集合所有的project和tasks,一个tasks包含了一系列动作，然后它们将会按照顺序执行，一个动作就是一段被执行的代码，很像Java中的方法。

## 构建的生命周期
1. 初始化阶段：project实例在这儿创建，如果有多个模块，即有多个build.gradle文件，多个project将会被创建。
2. 配置阶段：该阶段，build.gradle脚本将会执行，为每个project创建和配置所有的tasks
3. 执行阶段：这一阶段，gradle会决定哪一个tasks会被执行，哪一个tasks会被执行完全依赖开始构建时传入的参数和当前所在的文件夹位置有关。

**签名配置**
```java
  signingConfigs {
        //debug 是as默认的公共签名
        release {
            storeFile file("keystore")
            storePassword STORE_PASSWORD
            keyAlias KEY_ALIAS
            keyPassword KEY_PASSWORD
        }

    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            //签名 一般情况这样配置就好了
            signingConfig signingConfigs.release
        }
    }
```

**BuildConfig和resources**
```java
android {
    buildTypes {
        debug {
            //可以在代码中直接使用
            buildConfigField "String", "API_URL",
               "\"http://test.example.com/api\""
               buildConfigField "boolean", "LOG_HTTP_CALLS", "true"
               //可以在android tools team中直接使用  
               //  android:text="@string/app_name"
               resValue "string", "app_name", "Example DEBUG"
     }
       release {
            buildConfigField "String", "API_URL",
                "\"http://example.com/api\""
               buildConfigField "boolean", "LOG_HTTP_CALLS","false"
                resValue "string", "app_name", "Example"
     }
     //继承现有版本 initWith()方法创建了一个新的版本的同时，复制所有存在的构建版本，类似继承。我们也可以复写该存在版本的所有属性
     staging.initWith(buildTypes.relese)
     staging{
            //applicationId后缀 如：构建付费版本 staging 
            applicationIdSuffix ".staging"
            versionNameSuffix "-staging"
     } 
 }
```
**资源文件优先级**
资源和mainfests的优先级是这样的：
![](https://github.com/jiashuaishuai/GradleDemo/blob/master/15476188913749.jpg)

如果一个资源在main中和在flavor中定义了，那么那个在flavor中的资源有更高的优先级。这样那个在flavor文件夹中的资源将会被打包到apk。而在依赖项目申明的资源总是拥有最低优先级。


**productFlavors构建变体**
举例：
APK要配置不同厂商不同机型，现在有两个厂商需要适配，每个厂商有高配，中配，低配三个机型，规则
```java
flavorDimensions "cooperator","model"
productFlavors{
    //厂商维度
    cooperatorA{
        applicationId "com.ckj.myapplication1"
        //可以定义manifest变量
        manifestPlaceholders = [UMENG_CHANNEL_VALUE:"channel1"]
        versionCode 1
        versionName "1.0"
        //配置常量 编译生成（app/build/generated/source/buildConfig/BuildConfig.java）中常量
         buildConfigField "boolean", "SUPPORT_AUTO_UPDATE_FEATURE", "true"
        flavorDimension "cooperator"
    }
    cooperatorB{
        buildConfigField "boolean", "SUPPORT_AUTO_UPDATE_FEATURE", "false"
        flavorDimension "cooperator"
    }
    //机型维度
    modelA{
        flavorDimension "model"
    }
    modelB{
        flavorDimension "model"
    }
    modelC{
        flavorDimension "model"
    }
}
```
最终编译对应12个BuildVariant：
```java
cooperatorAmodelADebug
cooperatorAmodelARelease
cooperatorAmodelBDebug
cooperatorAmodelBRelease
cooperatorAmodelCDebug
cooperatorAmodelCRelease
cooperatorBmodelADebug
cooperatorBmodelARelease
cooperatorBmodelBDebug
cooperatorBmodelBRelease
cooperatorBmodelCDebug
cooperatorBmodelCRelease
```
在AndroidManifest.xml中添加渠道变量
```java
<application
    ...>
    ...
    <meta-data
        android:name="UMENG_CHANNEL"
        android:value="${UMENG_CHANNEL_VALUE}" />
</application>

```
**sourceSets配置代码目录**
在module目录下创建"src/cooperatorA/java"和“src/cooperatorB/java”目录，与原有的"src/main/java"目录对称，在build.gradle中，通过sourceSets指定cooperatorA和cooperatorB的源码目录（debug目录和release会默认指定），在实际编译过程中，根据选择的Build Variant到指定的源码目录查找源码文件，并且将main目录下的源码和指定目录下的源码进行合并
`（注：”src/cooperatorA/java”和”src/cooperatorB/java”目录只存放差异类，共有的类依然是在src/main/java目录下）`
新版本gradle  创建同名`productFlavors`并且指定`flavorDimensions`自动识别目录,可以不用手动指定sourceSets，旧版本没测
```java
flavorDimensions "cooperator"
productFlavors {
    cooperatorA {
        dimension "cooperator"
    }
    cooperatorB {
        dimension "cooperator"
    }
}

sourceSets {
    cooperatorA {
        java.srcDirs = ['src/cooperatorA/java']
        //java.srcDir("src/cooperator/java")
        res.srcDir("src/cooperator/res")
        manifest.srcFile 'src/cooperator/AndroidManifest.xml'
        
        
        //指定文件 现在只知道这么多 
        manifest.srcFile 'src/main/AndroidManifest.xml'
        java.srcDirs = ['src/main/java']
        resources.srcDirs = ['src/main/java']
        aidl.srcDirs = ['src/main/java']
        renderscript.srcDirs = ['src/main/java']
        res.srcDirs = ['src/main/res']
        assets.srcDirs = ['src/main/assets']
        jniLibs.srcDirs = ['libs']
    }
    cooperatorB {
        java.srcDirs = ['src/cooperatorB/java']
    }
}
```
**全局配置**
```java
allprojects {
    repositories {
        //默认仓库
        jcenter()
        mavenCentral()
        google()
        //远程仓库
        maven { url 'https://jitpack.io' }
        //带权限远程仓库
         maven {
           url "http://repo.acmecorp.com/maven2"
           credentials {
               username 'user'
               password 'secretpassword'
           }
        } 
    }
}
```

**依赖管理**
```java
//groovy语法
dependencies {
       compile 'com.google.code.gson:gson:2.3'
       compile 'com.squareup.retrofit:retrofit:1.9.0'
       //本地依赖
       compile files('libs/domoarigato.jar')
       compile fileTree('libs')
       //gradle给的默认
       compile fileTree(dir: 'libs', include: ['*.jar'])
       //依赖工程
       compile project(':library')
}
//完整版
dependencies {
      compile group: 'com.google.code.gson', name: 'gson', version:'2.3'
      compile group: 'com.squareup.retrofit', name: 'retrofit'
           version: '1.9.0'
     }
//动态版本
dependencies {
       //最新生产版本
       compile 'com.android.support:support-v4:22.2.+'
       //最新minor版本，并且其最小版本号为2
       compile 'com.android.support:appcompat-v7:22.2+'
       //最新library
       compile 'com.android.support:recyclerview-v7:+'
}
     
```
**清除build目录**
```java
// 运行gradle clean时，执行此处定义的task。
// 该任务继承自Delete，删除根目录中的build目录。
// 相当于执行Delete.delete(rootProject.buildDir)。
// gradle使用groovy语言，调用method时可以不用加（）。
task clean(type: Delete) {
    delete rootProject.buildDir
}
```

参考：https://segmentfault.com/a/1190000004229002
插件开发：https://blog.csdn.net/sbsujjbcy/article/details/50782830
