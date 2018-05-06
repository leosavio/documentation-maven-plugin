#!/bin/bash

# DOCDIR, specifies location of Config.groovy (doctoolchain project configuration file).
DOCDIR="$1"
which drawio-batch

set -u
set -e
set -o pipefail
SCRIPTS="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
shift

for filename in ${DOCDIR}/*.xml; do
    if [ -f ${filename} ]; then
        drawio-batch --quality 100 --scale 4 "${filename}" ${DOCDIR}/$(basename "${filename}" ".xml")".png"
    fi
done
