<?php
#set ( $parentFQNS = '' )
#set ( $docMethod = '' )
#set ( $docMethods = [] )
#set ( $parent = '' )
#set ( $namespace = '' )
#set ( $function = {} )
#set ( $functions = [] )
#set ( $classVariable = {} )
#set ( $classVariables = [] )
#set ( $arg = $project )
#if ( $bundleName )
#set ( $bundle = $bundleName )
#end

#if ( $NAME.endsWith('Facade') )
#parse ( 'PHP Spryker Zed Facade' )
#elseif ( $NAME.endsWith('Controller') )
#parse ( 'PHP Spryker Zed Controller' )
#elseif ( $NAME.endsWith('QueryContainer') )
#parse ( 'PHP Spryker Zed QueryContainer' )
#elseif ( $NAME.endsWith('Factory') )
#parse ( 'PHP Spryker Zed Factory')
#elseif ( $NAME.endsWith('Plugin') )
#parse ( 'PHP Spryker Zed Plugin' )
#elseif ( $NAME.endsWith('Bridge') )
#parse ( 'PHP Spryker Zed Bridge' )
#elseif ( $NAME.endsWith('Config') )
#parse ( 'PHP Spryker Zed Config' )
#elseif ( $NAME.endsWith('DependencyProvider') )
#parse ( 'PHP Spryker Zed DependencyProvider' )
#end
namespace $namespace;

#if ( $parentFQNS != '' )
use $parentFQNS;
#end

#if ( $docMethods != [] )
/**
#foreach ($docMethod in $docMethods.keySet())
 * @method $docMethod $docMethods.get($docMethod)
#end
 */
#end
class ${NAME} #if ( $parent != '' )extends $parent #end

{

#if ( $classVariables != [] )
#foreach ( $classVariable in $classVariables)
    $classVariable.get('visibility') ${DS}$classVariable.get('name');
#end
#end

#if ( $functions != [] )
#foreach ( $function in $functions )
    $function.get('visibility') function $function.get('name')($function.get('arguments'))
    {
    }
#end
#end

}
