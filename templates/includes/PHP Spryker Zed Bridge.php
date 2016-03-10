#set ( $bundle = $NAME.replace('Bridge', '') )
#set ( $namespaceBundle = $project + '\Zed\' + $bundle )
#set ( $namespace = $namespaceBundle  + '\Communication\Dependency\Facade')
#set ( $classVariables = [
    {
        'visibility' : 'private', 
        'name' : 'facade'
    }
] )
#set ( $functions = [
    {
        'visibility' : 'public',
        'name' : '__construct',
        'arguments' : '$facade'
    }
] )