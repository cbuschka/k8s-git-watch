#!/bin/bash

kubectl --context kind-kind exec gitwatch-controller -- "$@"

