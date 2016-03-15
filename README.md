# idea-php-spryker-plugin

## * Attention *

- Currently the templates have to be installed manually, the repository is not yet a plugin for PhpStorm.
- Only it is only possible to create Zed Classes for now.

## Installation:

1. Clone the repository.
2. Copy the templates to your [WebIdeXX-directory](https://www.jetbrains.com/phpstorm/help/directories-used-by-phpstorm-to-store-settings-caches-plugins-and-logs.html).

```
 cp /path/to/idea-php-spryker-plugin/templates/Spryker\ Zed.php /path/to/WebIdeXX/fileTemplate/
 cp /path/to/idea-php-spryker-plugin/templates/includes/* /path/to/WebIdeXX/fileTemplate/includes/
 
```

## Usage:

1. Browse to your project directory structure in PhpStorm.
2. Right-click on the target-folder > New > Spryker Zed
3. Enter filename (e.g. ProductBusinessFactory ) & project namespace (eg. Pyz or Spryker):
    -> The template will automatically match the suffix of the filename and creates the class-properties of the type.

Note: For Controller and Plugins it is also possible to enter the bundle-name, as for those classes the name may differ from the bundle-name. 

Currently supported Spryker Classes:

```
Bridge
Config
Controller (bundle-name can be set)
Plugin (bundle-name can be set)
DependencyProvider
Facade
BusinessFactory
CommunicationFactory
PersistenceFactory
QueryContainer
```

## Example:

- Filename: TestBusinessFactory
- Project: Pyz

Generates:

```php

<?php

namespace Pyz\Zed\Test\Business;

use Spryker\Zed\Kernel\Business\AbstractBusinessFactory;

/**
 * @method \Pyz\Zed\Test\Persistence\TestQueryContainer getQueryContainer()
 * @method \Pyz\Zed\Test\TestConfig getConfig()
 */
class TestBusinessFactory extends AbstractBusinessFactory 
{

}


```

## Comments?

Right now the repository contains just the first idea of something that should become a proper SprykerPhpStorm-Plugin, so if you have any ideas how to improve/extend please let me know.


