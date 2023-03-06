# CLI Genie
CLI Genie is a tool that assists users in writing CLI commands using their native language through OpenAI's GPT-3 API.

To put it simply, CLI Genie helps users who are not comfortable with writing commands in English to do so using their preferred language. By utilizing OpenAI's GPT-3 API, CLI Genie can provide accurate and relevant commands based on the user's request. It is important to note, however, that as with any software that uses machine learning or AI, there may be limitations and potential errors.

The CLI Genie is powered by OpenAI's advanced language model called gpt-3.5-turbo. It performs similarly to text-davinci-003 but is 10% cheaper per token.

## Installation

### Requirements
- git
- Java 11 or higher
- OpenAI API key

### Linux and Mac
```
git clone https://github.com/JM-Lab/cli-genie.git
cd cli-genie
./gradlew install
sudo cp bin/cg /usr/local/bin
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
cg [instructions in mother tongue]
```
## Examples
Here's examples of the usage of CLI Genie in various languages

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
