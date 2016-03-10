#if ( $NAME.endsWith('BusinessFactory'))
  #set ( $bundleLayer = 'Business' )
  #set ( $bundle = $NAME.replace('BusinessFactory', '') )
  #set ( $namespaceBundle = $project + '\Zed\' + $bundle )
  #set ( $namespace = $namespaceBundle + '\' + $bundleLayer )
  #set ( $parent = 'Abstract' + $bundleLayer + 'Factory' )
  #set ( $parentFQNS = 'Spryker\Zed\Kernel\' + $bundleLayer + '\Abstract' + $bundleLayer + 'Factory' )
  #set ( $bundleQueryContainer = '\' + $namespaceBundle + '\Persistence\' + $bundle + 'QueryContainer' )
  #set ( $bundleConfig = '\' + $namespaceBundle + '\' + $bundle + 'Config' )
  #set ( $docMethods = {
      $bundleQueryContainer : 'getQueryContainer()',
      $bundleConfig : 'getConfig()'
  } )
#elseif ( $NAME.endsWith('CommunicationFactory') )
  #set ( $bundleLayer = 'Communication' )
  #set ( $bundle = $NAME.replace('CommunicationFactory', '') )
  #set ( $namespaceBundle = $project + '\Zed\' + $bundle )
  #set ( $namespace = $namespaceBundle + '\' + $bundleLayer )
  #set ( $parent = 'Abstract' + $bundleLayer + 'Factory' )
  #set ( $parentFQNS = 'Spryker\Zed\Kernel\' + $bundleLayer + '\Abstract' + $bundleLayer + 'Factory' )
  #set ( $bundleQueryContainer = '\' + $namespaceBundle + '\Persistence\' + $bundle + 'QueryContainer' )
  #set ( $bundleFacade = '\' + $namespaceBundle + '\Business\' + $bundle + 'Facade' )
  #set ( $bundleConfig = '\' + $namespaceBundle + '\' + $bundle + 'Config' )
  #set ( $docMethods = {
      $bundleQueryContainer : 'getQueryContainer()',
      $bundleFacade : 'getFacade()',
      $bundleConfig : 'getConfig()'
  } )
#elseif ( $NAME.endsWith('PersistenceFactory') )
  #set ( $bundleLayer = 'Persistence' )
  #set ( $bundle = $NAME.replace('PersistenceFactory', '') )
  #set ( $namespaceBundle = $project + '\Zed\' + $bundle )
  #set ( $namespace = $namespaceBundle + '\' + $bundleLayer )
  #set ( $parent = 'Abstract' + $bundleLayer + 'Factory' )
  #set ( $parentFQNS = 'Spryker\Zed\Kernel\' + $bundleLayer + '\Abstract' + $bundleLayer + 'Factory' )
  #set ( $bundleConfig = '\' + $namespaceBundle + '\' + $bundle + 'Config' )
  #set ( $docMethods = {
      $bundleConfig : 'getConfig()'
  } )
#end

