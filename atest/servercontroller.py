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
import xmlrpc
import xmlrpc.client
import time
import subprocess
import socket
from os.path import abspath, dirname, exists, join
import os
import sys
import glob


BASE = dirname(abspath(__file__))


def start(libraries=
        'org.robotframework.examplelib.FullDynamic:/FullDynamic,' + \
        'org.robotframework.examplelib.MinDynamic:/MinDynamic,' + \
        'org.robotframework.examplelib.Static:/Static' ):
    if not os.path.exists(os.path.join(BASE, 'libs', 'target', 'examplelib-jar-with-dependencies.jar')):
        cmd = 'mvn -f "%s" clean package' % os.path.join(BASE, 'libs', 'pom.xml')
        print('Building the test libraries with command:\n%s' % cmd)
        subprocess.call(cmd, shell=True)
    files = glob.glob(os.path.join(dirname(BASE), 'target') + os.sep + '*jar-with-dependencies.jar')
    if not files:
        raise Exception('Build jrobotremoteserver including the standalone jar first')
    rs_path = os.path.join(dirname(BASE), 'target', files[0])
    tl_path = os.path.join(BASE, 'libs', 'target', 'examplelib-jar-with-dependencies.jar')
    os.environ['CLASSPATH'] = rs_path + os.pathsep + tl_path
    print('CLASSPATH: %s' % os.environ['CLASSPATH'])
    results = _get_result_directory()
    port = "8270"
    args = ['java', 'org.robotframework.remoteserver.RemoteServer', '--port', port]
    libraries = [x.strip() for x in libraries.split(',')]
    paths = [x.partition(':')[2] for x in libraries]
    for lib in libraries:
        args.extend(['--library', lib])
        print('adding library %s on path %s' % (lib.split(':')[0], lib.split(':')[1]))
    with open(join(results, 'server.txt'), 'w') as output:
        server = subprocess.Popen(args,
                                  stdout=output, stderr=subprocess.STDOUT,
                                  env=_get_environ())
    for path in paths:
        if not test(port, path, attempts=15):
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


def test(port, path, attempts=1):
    url = 'http://localhost:%s%s' % (port, path)
    for i in range(int(attempts)):
        if i > 0:
            time.sleep(1)
        try:
            ret = xmlrpc.client.ServerProxy(url).run_keyword('get_server_language', [])
        except socket.error:
            pass
        except xmlrpc.Error:
            errmsg = err.faultString
            break
        else:
            print("Remote server running on port %s, path %s" % (port, path))
            return True
    print("Failed to connect to remote server on port %s path %s: %s" % (port, path, errmsg))
    return False


def stop(port=8270, path="/"):
    if test(port, path):
        server = xmlrpclib.ServerProxy('http://localhost:%s%s' % (port, path))
        server.stop_remote_server()
        print("Remote server on port %s path %s stopped" % (port, path))


if __name__ == '__main__':
    if len(sys.argv) == 1 or '-h' in sys.argv or '--help' in sys.argv:
        sys.exit(__doc__)
    mode = sys.argv[1]
    args = sys.argv[2:]
    try:
        {'start': start, 'stop': stop, 'test': test}[mode](*args)
    except (KeyError, TypeError):
        sys.exit(__doc__)
