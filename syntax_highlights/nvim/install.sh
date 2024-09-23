#!/usr/bin/env bash

# a small script for installing the nvim extension

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
cd $SCRIPT_DIR

printf "Enter the neovim dist you are using (nvim for default): "
read dist

if [[ -d "$HOME/.config/$dist" ]]; then
  NVIM_CONFIG_DIR="$HOME/.config/$dist"
elif [[ -d "$HOME/.$dist" ]]; then
  NVIM_CONFIG_DIR="$HOME/.$dist"
else
  echo "Error: Unable to find NeoVim configuration path in $HOME!"
fi

set -xe

mkdir -p $NVIM_CONFIG_DIR/lua
mkdir -p $NVIM_CONFIG_DIR/syntax
cp zulu.lua $NVIM_CONFIG_DIR/lua/
cp -r syntax/* $NVIM_CONFIG_DIR/syntax

PACKAGE_INIT_STMT="\n-- auto-generated by \`install.sh\`\nrequire('zulu').setup()"

if ! [[ -f "$NVIM_CONFIG_DIR/init.lua" ]]; then
  echo "Error: Unable to find init.lua in $NVIM_CONFIG_DIR."
  echo "please paste the following lua code into your configuratgion files in order to activate the zulu package:"
  echo "    $PACKAGE_INIT_STMT"
  exit 0
fi

if grep -q "require('zulu')" "$NVIM_CONFIG_DIR/init.lua" || grep -q "require(\"zulu\")" "$NVIM_CONFIG_DIR/init.lua"; then
  echo "Note: Plugin already activated"
else
  echo -e $PACKAGE_INIT_STMT >>$NVIM_CONFIG_DIR/init.lua
fi

echo "zulu NeoVim plugin successfully installed!"
