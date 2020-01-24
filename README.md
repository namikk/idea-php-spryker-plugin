Spryker PhpStorm Plugin + Class Override Extension
========================

Manually Install Plugin
------------------------

 1. Download https://github.com/namikk/idea-php-spryker-plugin/blob/class-override/idea-php-spryker-plugin.jar
 2. Plugin must be manually installed as it's not available on the marketplace: Go to 'Preferences | Plugins', click on settings icon and then on 'Install Plugin fom Disk...'. Use the 'idea-php-spryker-plugin.jar' file that you just downloaded.

Features
------------------------

The Spryker-Plugin was built to improve the daily development-experience with the Spryker-Framework.


## 1. Generate DocBlocks automatically

Some of the Spryker base classes, have very special DocBlocks. In order to generate them with the help of the Plugin, browse to the class, press `alt` + `enter` and select `Update Spryker DocBlock`.


![Generate DocBlock](https://raw.githubusercontent.com/project-a/idea-php-spryker-plugin/master/docs/update_docblock.gif)

## 2. Generate Spryker-Classes

It also possible to generate base classes of Spryker automatically.

- right-click the directory in the project structure
- select `New` and (if possible) the file you want to create will appear in the menu

![Generate DocBlock](https://raw.githubusercontent.com/project-a/idea-php-spryker-plugin/master/docs/create_spryker_file.gif)

## 3. Bundle Generation

To create a new bundle:

- right-click the app directory (Yves, Zed or Client)
- select `New and Create (Yves|Zed|Client) Bundle`

![Generate DocBlock](https://raw.githubusercontent.com/project-a/idea-php-spryker-plugin/master/docs/create_spryker_bundle.gif)

## 4. Class Override

Class override allows you to generate all necessary code segments for overriding a spryker class with a single action.
1. Open spryker class, go to `Edit menu`, click on `Override Spryker Class`.
2. Right click on spryker class file in `Project view`, click on `Override Spryker Class`.
3. Right click on a a spryker `class method`, click on `Override Class Method` (not implemented).

## 5. Config

Plugin config can be found in 'Preferences | Languages & Frameworks | PHP | Spryker'.
1. `Global - Enable plugin toggle`: Enable/disable plugin for current project. (Not implemented)
2. `Class Override - Root directory`: Custom project root directory can be defined by entering it in the input field. (Note: Browse path button functionality not implemented)
3. `Class Override - Exclude parent content on class override`: Newly created override classes will not contain any content from the parent class (methods, variables, etc.). By default all override methods call their respective parent methods. 
4. `Class Override - Allow any namespace`: Enable class override functionality for non-spryker vendor files. (unstable)
