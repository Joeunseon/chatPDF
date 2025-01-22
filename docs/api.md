# 📑 API 문서

<br>

## 1. 소개 
이 문서는 PDF 파일을 대화형으로 다룰 수 있는 ChatPDF API 사용법을 설명합니다.

<br>

## 2. 공통 사항
- API 공식 문서: [바로가기](https://www.chatpdf.com/docs/api/backend) <br>
  URL: `https://www.chatpdf.com/docs/api/backend` 
- API Base URL: `https://api.chatpdf.com/v1`
- 요청 방식: 모든 요청은 `POST` 방식으로 이루어지며, `API_KEY`가 필요합니다.
- 제한 사항: PDF 파일 당 2,000 페이지 또는 32MB로 제한됩니다.

<br>

## 3. 엔드포인트 목록
| 엔드포인트           | 설명                           |
|---------------------|--------------------------------|
| `/sources/add-url`  | [Add PDF via URL](#41-add-pdf-via-url) |
| `/sources/add-file` | [Add PDF via File Upload](#42-add-pdf-via-file-upload) |
| `/chats/message`    | [Chat with PDF](#43-chat-with-pdf) |
| `/sources/delete`   | [Delete PDF](#44-delete-pdf) |

<br>

## 4. 엔드포인트 상세
### 4.1 Add PDF via URL
PDF 파일을 URL을 통해 ChatPDF로 보낼 수 있습니다.

#### 요청
`POST` /sources/add-url
```json
headers: {
    "x-api-key": "sec_xxxxxx",
    "Content-Type": "application/json"
}

body: { 
    "url": "PDF 파일 URL" 
}
```

#### 응답
```json
{ 
    "sourceId": "src_xxxxxx" 
}
```

<br>

### 4.2 Add PDF via File Upload
멀티 파트 양식 데이터로 PDF 파일을 ChatPDF로 보낼 수 있습니다.

#### 요청
`POST` /sources/add-file
```json
headers: {
    "x-api-key": "sec_xxxxxx"
}

body: { 
    "file": "@/path/to/file.pdf"
}
```

#### 응답
```json
{ 
    "sourceId": "src_xxxxxx" 
}
```

<br>

### 4.3 Chat with PDF
채팅 메시지를 PDF 파일과 주고받을 수 있습니다.

#### 공통

|엔드포인트  | `POST` /chats/message |
|-----------|-----------------------|

```json
headers: {
    "x-api-key": "sec_xxxxxx",
    "Content-Type": "application/json"
}
```

<br>

#### 4.3.1 일반 메시지 전송
#### 요청
```json
{
  "sourceId": "src_xxxxxx",
  "messages": [
    {
      "role": "user",
      "content": "요청 메시지"
    }
  ]
}
```

#### 응답
```json
{ 
    "content": "응답 메시지" 
}
```

<br>

#### 4.3.2 연관 질문 전송
#### 요청
```json
{
  "sourceId": "src_xxxxxx",
  "messages": [
    {
      "role": "user",
      "content": "요청 메시지"
    },
    {
      "role": "assistant",
      "content": "응답 메시지"
    },
    {
      "role": "user",
      "content": "요청 메시지"
    }
  ]
}
```
한 번의 요청으로 최대 6개의 메시지를 포함 할 수 있습니다.

#### 응답
```json
{ 
    "content": "응답 메시지" 
}
```

<br>

#### 4.3.3 참조 포함하여 응답 메시지 받기
#### 요청
```json
{
  "referenceSources": true,
  "sourceId": "src_xxxxxx",
  "messages": [
    {
      "role": "user",
      "content": "요청 메시지"
    }
  ]
}
```

#### 응답
```json
{
  "content": "응답 메시지 [P2] [P5]",
  "references": [
    { "pageNumber": 2 },
    { "pageNumber": 5 }
  ]
}
```
응답에는 `[P <N>]` 형식의 인라인 참조가 포함됩니다.

<br>

### 4.4 Delete PDF
sourceId를 사용하여 ChatPDF에서 하나 이상의 PDF 파일을 삭제할 수 있습니다.

#### 요청
`POST` /sources/delete
```json
headers: {
    "x-api-key": "sec_xxxxxx",
    "Content-Type": "application/json"
}

body: { "sources": ["src_xxxxxx"] }
```

#### 응답
```json
This endpoint returns an empty response.
```

<br>
<br>

## 🖇️ 프로젝트 문서
### 🗂️ Page 👉 [바로가기](docs/page.md)
### 🗂️ DATABASE 👉 [바로가기](docs/database.md)
### 📑 API 문서
### 📑 README 👉 [바로가기](../README.md)