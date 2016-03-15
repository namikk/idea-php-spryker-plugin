#if ( !$bundleName )
#set ( $bundle = $NAME.replace('Plugin', '') )
#end
#set ( $namespaceBundle = $project + '\Zed\' + $bundle )
#set ( $namespace = $namespaceBundle  + '\Communication')
#set ( $bundleFactory = '\' + $namespaceBundle + '\Communication\' + $bundle + 'CommunicationFactory' )
#set ( $bundleFacade = '\' + $namespaceBundle + '\Business\' + $bundle + 'Facade' )
#set ( $parent = 'AbstractPlugin' )
#set ( $parentFQNS = 'Spryker\Zed\Kernel\Communication\AbstractPlugin' )
#set ( $docMethods = { 
    $bundleFactory : 'getFactory()', 
    $bundleFacade : 'getFacade()' 
} )
