#set ( $bundle = $NAME.replace('Controller', '') )
#set ( $namespaceBundle = $project + '\Zed\' + $bundle )
#set ( $namespace = $project + '\Zed\' + $bundle  + '\Communication\Controller')
#set ( $parent = 'AbstractController' )
#set ( $bundleFactory = '\' + $namespaceBundle + '\Communication\' + $bundle + 'CommunicationFactory' )
#set ( $bundleFacade = '\' + $namespaceBundle + '\Business\' + $bundle + 'Facade' )
#set ( $bundleQueryContainer = '\' + $namespaceBundle + '\Persistence\' + $bundle + 'QueryContainer' )
#set ( $parentFQNS = 'Spryker\Zed\Application\Communication\Controller\AbstractController' )
#set ( $docMethods = {
    $bundleFactory : 'getFactory()',
    $bundleFacade : 'getFacade()',
    $bundleQueryContainer : 'getQueryContainer()'
} )