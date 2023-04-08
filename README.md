# CLI Genie
CLI Genie is a tool that helps users write CLI commands using their native language with the addition of Copilot in the terminal shell through OpenAI's GPT API.

To put it simply, CLI Genie helps users who are not comfortable with writing commands in English to do so using their preferred language. By utilizing OpenAI's GPT API, CLI Genie can provide accurate and relevant commands based on the user's request.

CLI Genie is especially useful for IT engineers or software engineers who primarily use terminal shells in Linux or Mac environments. With the addition of Copilot, users can expect even more streamlined and efficient command writing processes.

![Screenshot](https://raw.githubusercontent.com/JM-Lab/cli-genie/main/screenshot.gif)
## Key Features of CLI Genie
1. Native Language Input Support
   * CLI Genie understands and processes user input in their native language.

2. OS and Version-Awareness
   * CLI Genie suggests appropriate CLI commands or recommendations based on the user's operating system and version.
   
3. Automatic Copying of GPT's Response
   * Users can easily paste the generated content in response to their queries using CLI Genie.

4. Used OpenAI API's Server-Sent Events (SSE) for response handling.
   * CLI Genie can receive updates as they happen, allowing for a more interactive and responsive experience.
   
5. General Question-Answering with GPT
   * Users can ask general questions to CLI Genie using CLI commands 'cgg' (same as 'cg -g') just like ChatGPT.

## Installation
### Requirements
- git
- Java 11 or higher
- OpenAI API key

### Linux and Mac
#### Auto-install with dependencies
You can install cli-genie with its dependencies by running the following command:
```
curl https://raw.githubusercontent.com/JM-Lab/cli-genie/main/script/install-cli-genie.sh | sudo sh
export OPENAI_API_KEY=[your key]
```
#### Uninstall
You can uninstall cli-genie by running the following command:
```
curl https://raw.githubusercontent.com/JM-Lab/cli-genie/main/script/uninstall-cli-genie.sh | sh
```
#### Manual installation
To install cli-genie manually, run the following commands:
```
cd ~/
git clone https://github.com/JM-Lab/cli-genie.git
cd cli-genie
./gradlew install
sudo cp bin/cg /usr/local/bin
echo "alias cgg='cg -g'" >> ~/.bashrc
source ~/.bashrc
export OPENAI_API_KEY=[your key]
```
### Windows
```
git clone https://github.com/JM-Lab/cli-genie.git
cd cli-genie
.\gradlew.bat install
copy bin\cg.bat C:\Windows\System32
set OPENAI_API_KEY=[your key]
```
#### OpenAI API key
To use CLI Genie, you need to obtain the OPENAI_API_KEY from https://platform.openai.com and set it as an environment variable before the first run. The key will be stored in [USER HOME]/.cg/openai-api-key and used for subsequent runs.

## Usage
You can run CLI Genie by using the `cg` (short for CLI Genie). Available commands can be input in the user's mother 
tongue. 
The usage of the cg command is as follows:
```
usage: cg [Options] <instructions in mother tongue>
* Use \ or enclose special characters in instructions.

Example:
cg replace the letters "abc" with "cba" in the file test.txt

Options:
 -g,--general   General query to GPT
 -h,--help      Print help message
 -n,--no        Do not use copy to clipboard

To ask general questions to GPT, use 'cgg' (same as 'cg -g') in linux or mac.
CLI Genie: https://github.com/JM-Lab/cli-genie
```
## Examples
Here is an example of commands that can be given to CLI Genie in various languages, all of which produce the same response:
```
sed -i '' 's/abc/cba/g' test.txt

Copied GPT's response to clipboard. Paste shortcut: Command + V (MacOS).
```

### Korean
```
cg test.txt 파일에서 "abc"를 "cba"로 바꿔주세요
```

### English
```
cg replace the letters "abc" with "cba" in the file test.txt
```

### Mandarin Chinese
```
cg 在test.txt文件中用"cba"替换"abc"
```

### Hindi
```
cg test.txt फ़ाइल में "abc" को "cba" से बदलें
```

### Spanish
```
cg reemplazar las letras "abc" por "cba" en el archivo test.txt
```

### Arabic
```
cg استبدل الحروف "abc" بـ "cba" في الملف test.txt
```

### Bengali
```
cg test.txt ফাইলের "abc" অক্ষরগুলি "cba" দিয়ে পরিবর্তন করুন
```

### French
```
cg remplacer les lettres "abc" par "cba" dans le fichier test.txt
```

### Russian
```
cg заменить буквы "abc" на "cba" в файле test.txt
```

### Portuguese
```
cg substituir as letras "abc" por "cba" no arquivo test.txt
```

### Urdu
```
cg test.txt فائل میں حروف "abc" کو "cba" سے تبدیل کریں
```

### Japanese
```
cg test.txt ファイル内の文字列 "abc" を "cba" に置き換える
```

### Vietnamese
```
cg thay thế các chữ cái "abc" bằng "cba" trong tệp test.txt
```