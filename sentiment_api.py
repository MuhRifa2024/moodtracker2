# sentiment_api.py
from fastapi import FastAPI, Request
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

app = FastAPI()
tokenizer = AutoTokenizer.from_pretrained("nlptown/bert-base-multilingual-uncased-sentiment")
model = AutoModelForSequenceClassification.from_pretrained("nlptown/bert-base-multilingual-uncased-sentiment")

@app.post("/predict")
async def predict(request: Request):
    data = await request.json()
    text = data.get("text", "")
    inputs = tokenizer.encode_plus(text, return_tensors="pt", truncation=True)
    outputs = model(**inputs)
    probs = torch.nn.functional.softmax(outputs.logits, dim=1)
    stars = torch.argmax(probs) + 1
    return {"stars": stars.item()}