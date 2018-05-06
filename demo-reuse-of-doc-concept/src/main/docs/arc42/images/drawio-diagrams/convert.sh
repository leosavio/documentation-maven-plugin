#!/bin/bash

for filename in *.xml; do
    drawio-batch --quality 100 --scale 2 "${filename}" $(basename "${filename}" ".xml")".png"
done