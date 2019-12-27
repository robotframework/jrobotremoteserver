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
import java.util.List;
import java.util.Map;

import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.reflection.ArgumentCollector;
import org.robotframework.javalib.reflection.IArgumentCollector;
import org.robotframework.javalib.reflection.KeywordInvoker;

public class KeywordOverload implements Keyword {

   private final Method method;
   private final Object obj;

   public KeywordOverload(Object obj, Method method) {
       this.obj = obj;
       this.method = method;
   }

   public Object execute(List args, Map kwargs) {
       try {
           List collectedArguments = createArgumentCollector().collectArguments(args, kwargs);
           Object[] reflectionArgsArray = collectedArguments != null ? collectedArguments.toArray() : null;
           return method.invoke(obj, reflectionArgsArray);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

    @Override
    public List<String> getArgumentTypes() {
        return null;
    }

    public boolean canExecute(List args, Map kwargs) {
       try {
           List collectedArguments = createArgumentCollector().collectArguments(args, kwargs);
           for (int i = 0; i < args.size(); i++) {
               if ( (collectedArguments.get(i) == null) && (args.get(i) != null) ) {
                   return false;
               }
           }
           return true;
       }
       catch (Exception e) {
           return false;
       }
   }

   protected IArgumentCollector createArgumentCollector() {
       return new ArgumentCollector(method.getParameterTypes(), new KeywordInvoker(this.obj, this.method).getParameterNames());
   }

}
