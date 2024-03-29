#!/bin/bash

if [[ "$OSTYPE" == "darwin"* ]]; then
    # check if Homebrew is installed
    if ! command -v brew &> /dev/null
    then
        echo "Homebrew is not installed. Installing Homebrew..."
        # install Homebrew
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi
fi

# check if java is installed
if ! command -v java &> /dev/null
then
    echo "Java 11 or later is not installed. Installing Java..."
    # install java
    if command -v apt-get &> /dev/null
    then
        sudo apt-get update
        sudo apt-get install default-jre -y
    elif command -v yum &> /dev/null
    then
        sudo yum install java-11-openjdk-devel -y
    elif command -v dnf &> /dev/null
    then
        sudo dnf install java-11-openjdk-devel -y
    elif command -v pacman &> /dev/null
    then
        sudo pacman -S jdk11-openjdk --noconfirm
    elif command -v brew &> /dev/null
    then
        brew install openjdk@11
    else
        echo "Unsupported OS. Please install Java 11 or later manually and try again."
        exit 1
    fi
fi

# check if git is installed
if ! command -v git &> /dev/null
then
    echo "Git is not installed. Installing Git..."
    # install git
    if command -v apt-get &> /dev/null
    then
        sudo apt-get update
        sudo apt-get install git -y
    elif command -v yum &> /dev/null
    then
        sudo yum install git -y
    elif command -v dnf &> /dev/null
    then
        sudo dnf install git -y
    elif command -v pacman &> /dev/null
    then
        sudo pacman -S git --noconfirm
    elif command -v brew &> /dev/null
    then
        brew install git
    else
        echo "Unsupported OS. Please install Git manually and try again."
        exit 1
    fi
fi

cd ~/

# clone the repository
git clone https://github.com/JM-Lab/cli-genie.git

# install cli-genie
cd cli-genie

if ./gradlew install; then
  echo "Gradle task succeeded"
  sudo cp bin/cg /usr/local/bin

  SHELL_TYPE=$(basename "$SHELL")

  if [ "$SHELL_TYPE" = "zsh" ]; then
    # add alias to bashrc
    echo "alias cgg='cg -g'" >> ~/.zshrc
    echo "alias cgt='cg -tc'" >> ~/.zshrc
    source ~/.zshrc
  else
    # add alias to bashrc
    echo "alias cgg='cg -g'" >> ~/.bashrc
    echo "alias cgt='cg -tc'" >> ~/.bashrc
    source ~/.bashrc
  fi

  echo "cli-genie installation is complete."
else
  echo "cli-genie installationk failed"
fi
