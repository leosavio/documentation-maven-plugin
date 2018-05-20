#!/bin/bash -l

# DOCDIR, specifies location of Config.groovy (doctoolchain project configuration file).
DOCDIR="$1"
which doctoolchain

set -u
set -e
set -o pipefail
SCRIPTS="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
shift

doctoolchain "${DOCDIR}" publishToConfluence -PconfluenceConfigFile=configuration/ConfluenceConfig.groovy --no-daemon
#  --debug
