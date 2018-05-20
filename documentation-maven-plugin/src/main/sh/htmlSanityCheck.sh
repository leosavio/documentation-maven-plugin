#!/bin/bash -l

# DOCDIR, specifies location of Config.groovy (doctoolchain project configuration file).
DOCDIR="$1"
which doctoolchain

doctoolchain "${DOCDIR}" htmlSanityCheck --no-daemon
#  --debug
