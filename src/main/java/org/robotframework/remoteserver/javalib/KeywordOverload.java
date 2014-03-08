/* Copyright 2014 Kevin Ormbrek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* This code is derived from JavalibCore 
 * Copyright 2008 Nokia Siemens Networks Oyj
 */
package org.robotframework.remoteserver.javalib;


import java.lang.reflect.Method;

import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.reflection.ArgumentConverter;
import org.robotframework.javalib.reflection.ArgumentGrouper;
import org.robotframework.javalib.reflection.IArgumentConverter;
import org.robotframework.javalib.reflection.IArgumentGrouper;

public class KeywordOverload implements Keyword {

   private final Method method;
   private final Object obj;

   public KeywordOverload(Object obj, Method method) {
       this.obj = obj;
       this.method = method;
   }

   public Object execute(Object[] args) {
       try {
           Object[] groupedArguments = createArgumentGrouper().groupArguments(args);
           Object[] convertedArguments = createArgumentConverter().convertArguments(groupedArguments);
           return method.invoke(obj, convertedArguments);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   public boolean canExecute(Object[] args) {
       try {
           Object[] groupedArguments = createArgumentGrouper().groupArguments(args);
           Object[] convertedArguments = createArgumentConverter().convertArguments(groupedArguments);
           for (int i = 0; i < args.length; i++) {
               if ( (convertedArguments[i] == null) && (args[i] != null) ) {
                   return false;
               }
           }
           return true;
       }
       catch (Exception e) {
           return false;
       }
   }

   protected IArgumentConverter createArgumentConverter() {
       return new ArgumentConverter(method.getParameterTypes());
   }
   
   protected IArgumentGrouper createArgumentGrouper() {
       return new ArgumentGrouper(method.getParameterTypes());
   }

}
