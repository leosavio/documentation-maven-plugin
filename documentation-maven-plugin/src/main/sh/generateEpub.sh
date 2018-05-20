#!/bin/bash -l

# DOCDIR, specifies location of Config.groovy (doctoolchain project configuration file).
DOCDIR="$1"
which doctoolchain

set -u
set -e
set -o pipefail
SCRIPTS="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
shift

"${SCRIPTS}"/generateDocbook.sh "${DOCDIR}" && doctoolchain "${DOCDIR}" convertToEpub --no-daemon
#  --debug
