#!/bin/bash

# DOCDIR, specifies location of Config.groovy (doctoolchain project configuration file).
DOCDIR="$1"
which doctoolchain

doctoolchain "${DOCDIR}" exportChangeLog --no-daemon
#  --debug
