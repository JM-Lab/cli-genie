#!/bin/bash

# remove alias from bashrc
sed -i.bak '/alias cgg/d' ~/.bashrc && rm ~/.bashrc.bak
sed -i.bak '/alias cgg/d' ~/.zshrc && rm ~/.zshrc.bak
sed -i.bak '/alias cgt/d' ~/.bashrc && rm ~/.bashrc.bak
sed -i.bak '/alias cgt/d' ~/.zshrc && rm ~/.zshrc.bak

# remove cli-genie installation
sudo rm /usr/local/bin/cg
sudo rm -rf ~/cli-genie
sudo rm -rf ~/.cg

echo "cli-genie has been uninstalled."
