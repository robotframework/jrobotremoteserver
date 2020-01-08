#!/usr/bin/env python

"""Script for running the remote server tests using different interpreters.

usage: run.py [runner] [libraries] [[options] datasources]

`runner` is the interpreter to use for running tests, defaulting to
python. `libraries` is a comma-delimited list of libraries in the form
class:port.

By default the test libraries will be a AnnotationLibrary, ClassPath library,
and a static API library. By default all tests under `tests` directory are
executed. This can be changed by giving data sources and options explicitly.
"""

import sys
import subprocess
from os.path import abspath, dirname, exists, join
from os import mkdir
from shutil import rmtree

import robot

import servercontroller
import statuschecker

BASE = dirname(abspath(__file__))
RESULTS = join(BASE, 'results')
if exists(RESULTS):
    rmtree(RESULTS)
mkdir(RESULTS)

clargs = sys.argv[1:]
if '-h' in clargs or '--help' in clargs:
    sys.exit(__doc__)

interpreter = clargs.pop(0) if clargs else 'python'
libraries = \
        'org.robotframework.examplelib.FullDynamic:/FullDynamic,' + \
        'org.robotframework.examplelib.MinDynamic:/MinDynamic,' + \
        'org.robotframework.examplelib.Static:/Static,' + \
        'org.robotframework.examplelib.MinDynamicKwargs:/MinDynamicKwargs'
if clargs:
    libraries = clargs.pop(0)

servercontroller.start(libraries)
libraries = [x.strip() for x in libraries.split(',')]
outputs = []

for entry in libraries:
    name, _, path = entry.partition(':')
    name = name.rsplit('.', 1)[1]
    OUTPUT = join(RESULTS, 'output-' + name + '.xml')
    outputs.append(OUTPUT)

    args = [interpreter, '-m', 'robot.run', '--name', name, '--variable', 'PATH:' + path,
            '--output', OUTPUT, '--log', 'NONE', '--report', 'NONE']
    if 'MinDynamic' in name:
        args.extend(['--exclude', 'argsknown'])
    if 'kwargs' in name.lower():
        args.extend(['--include', 'kwargs'])
    else:
        args.extend(['--exclude', 'kwargs'])
    args.extend(['--loglevel','DEBUG'])
    args.extend([join(BASE, 'tests')])
    print('Running tests with command:\n%s' % ' '.join(args))
    subprocess.call(args)

    print
    statuschecker.process_output(OUTPUT)
    
servercontroller.stop(8270, "/Static")
rc = robot.rebot(*outputs, outputdir=RESULTS)
if rc == 0:
    print('All tests passed')
else:
    print('%d test%s failed' % (rc, 's' if rc != 1 else ''))
