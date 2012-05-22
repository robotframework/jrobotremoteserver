#!/usr/bin/env python

"""Script for running the remote server tests using different interpreters.

usage: run.py server[:runner] [libraryfile=filename] [[options] datasources]

`server` is the only required argument and specifies the server interpreter
to use. `runner` is the interpreter to use for running tests, defaulting to
the server interpreter. `libraryfile` is the file name of the test library to
use, defaulting to StaticApiLibrary.py.

By default all tests under `tests` directory are executed. This can be
changed by giving data sources and options explicitly.
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


# clargs = sys.argv[1:]
# if not clargs or '-h' in clargs or '--help' in clargs:
    # sys.exit(__doc__)

# interpreters = clargs.pop(0)
# if ':' in interpreters:
    # server_interpreter, runner_interpreter = interpreters.rsplit(':', 1)
# else:
    # server_interpreter = runner_interpreter = interpreters
# if clargs and clargs[0].startswith('libraryfile='):
    # library_file = clargs.pop(0).split('=')[1]
# else:
    # library_file = 'StaticApiLibrary.py'
runner_interpreter = 'python'
outputs = []

libraries = ['org.robotframework.examplelib.FullDynamic:8270',
     'org.robotframework.examplelib.MinDynamic:8271',
     'org.robotframework.examplelib.Static:8272']

for entry in libraries:
    name, _, port = entry.partition(':')

    OUTPUT = join(RESULTS, 'output-' + name + '.xml')
    outputs.append(OUTPUT)
    servercontroller.start()

    #name = interpreters + '_-_' + library_file.rsplit('.', 1)[0]
    args = [runner_interpreter, '-m', 'robot.run', '--name', name, '--variable', 'PORT:' + str(port),
            '--output', OUTPUT, '--log', 'NONE', '--report', 'NONE']
    if 'min' in name.lower():
        args.extend(['--exclude', 'argsknown'])
    #args.extend(clargs or [join(BASE, 'tests')])
    args.extend([join(BASE, 'tests')])
    print 'Running tests with command:\n%s' % ' '.join(args)
    subprocess.call(args)

    servercontroller.stop(port)
    print
    statuschecker.process_output(OUTPUT)

rc = robot.rebot(*outputs, outputdir=RESULTS)
if rc == 0:
    print 'All tests passed'
else:
    print '%d test%s failed' % (rc, 's' if rc != 1 else '')
