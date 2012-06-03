#!/usr/bin/env python

"""Module/script for controlling remote server used in tests.

When used as module, provides `start`, `test`, and `stop` methods.
The server's stdin and stdout streams are redirected to results/server.txt

Usage:  servercontroller.py start|stop|test [args]

  start args: [libraries=default_libraries] formatted like class:port, ...
  test args:  [port=8270] [attempts=1]
  stop args:  [port=8270]
  
  default_libraries are a AnnotationLibrary, ClassPathLibrary, and static API
  library on ports 8270-8272.

Note: Starting from CLI leaves the terminal in a messed up state.
"""

from __future__ import with_statement
import xmlrpclib
import time
import subprocess
import socket
from os.path import abspath, dirname, exists, join
import os
import sys


BASE = dirname(abspath(__file__))


def start(libraries=
        'org.robotframework.examplelib.FullDynamic:8270,' + \
        'org.robotframework.examplelib.MinDynamic:8271,' + \
        'org.robotframework.examplelib.Static:8272' ):
    if not os.path.exists(os.path.join(BASE, 'libs', 'target', 'examplelib-jar-with-dependencies.jar')):
        cmd = 'mvn -f "%s" clean package' % os.path.join(BASE, 'libs', 'pom.xml')
        print 'Building the test libraries with command:\n%s' % cmd
        subprocess.call(cmd, shell=True)
    rs_path = os.path.join(dirname(BASE), 'target', 'jrobotremoteserver-jar-with-dependencies.jar')
    tl_path = os.path.join(BASE, 'libs', 'target', 'examplelib-jar-with-dependencies.jar')
    os.environ['CLASSPATH'] = rs_path + os.pathsep + tl_path
    print 'CLASSPATH: %s' % os.environ['CLASSPATH']
    results = _get_result_directory()
    args = ['java', 'org.robotframework.remoteserver.RemoteServer']
    ports = []
    libraries = [x.strip() for x in libraries.split(',')]
    for lib in libraries:
        args.extend(['--library', lib])
        ports.append(lib.split(':')[1])
        print 'adding library %s on port %s' % (lib.split(':')[0], lib.split(':')[1])
    with open(join(results, 'server.txt'), 'w') as output:
        server = subprocess.Popen(args,
                                  stdout=output, stderr=subprocess.STDOUT,
                                  env=_get_environ())
    for port in ports:
        if not test(port, attempts=15):
            server.terminate()
            raise RuntimeError('Starting remote server failed')

def _get_result_directory():
    path = join(BASE, 'results')
    if not exists(path):
        os.mkdir(path)
    return path

def _get_environ():
    environ = os.environ.copy()
    src = join(BASE, '..', 'src')
    environ.update(PYTHONPATH=src, JYTHONPATH=src, IRONPYTHONPATH=src)
    return environ


def test(port=8270, attempts=1):
    url = 'http://localhost:%s' % port
    for i in range(int(attempts)):
        if i > 0:
            time.sleep(1)
        try:
            ret = xmlrpclib.ServerProxy(url).run_keyword('get_server_language', [])
        except socket.error, (errno, errmsg):
            pass
        except xmlrpclib.Error, err:
            errmsg = err.faultString
            break
        else:
            print "%s remote server running on port %s" % (ret['return'], port)
            return True
    print "Failed to connect to remote server on port %s: %s" % (port, errmsg)
    return False


def stop(port=8270):
    if test(port):
        server = xmlrpclib.ServerProxy('http://localhost:%s' % port)
        server.stop_remote_server()
        print "Remote server on port %s stopped" % port


if __name__ == '__main__':
    if len(sys.argv) == 1 or '-h' in sys.argv or '--help' in sys.argv:
        sys.exit(__doc__)
    mode = sys.argv[1]
    args = sys.argv[2:]
    try:
        {'start': start, 'stop': stop, 'test': test}[mode](*args)
    except (KeyError, TypeError):
        sys.exit(__doc__)
