#set ( $bundle = $NAME.replace('QueryContainer', '') )
#set ( $namespaceBundle = $project + '\Zed\' + $bundle )
#set ( $namespace = $project + '\Zed\' + $bundle  + '\Persistence')
#set ( $parent = 'AbstractQueryContainer' )
#set ( $bundleFactory = '\' + $namespaceBundle + '\Persistence\' + $bundle + 'PersistenceFactory' )
#set ( $parentFQNS = 'Spryker\Zed\Kernel\Persistence\AbstractQueryContainer' )
#set ( $docMethods = {
    $bundleFactory : 'getFactory()'
} )