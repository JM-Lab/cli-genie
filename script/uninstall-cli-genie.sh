#!/bin/bash

# remove cgg alias from bashrc
sed -i.bak '/alias cgg/d' ~/.bashrc && rm ~/.bashrc.bak

# remove cli-genie installation
sudo rm /usr/local/bin/cg
sudo rm -rf ~/cli-genie
sudo rm -rf ~/.cg

echo "cli-genie has been uninstalled."
