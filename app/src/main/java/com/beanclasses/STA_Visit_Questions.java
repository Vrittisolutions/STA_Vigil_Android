package com.beanclasses;

public class STA_Visit_Questions {
    String QuesID;
    String Question;
    String ResponseType;
    String SelectionText;
    String Valuemin;
    String ValueMax;
    String answer;
    String ANSWER;
    String QuesCode;

    public String getANSWER() { return ANSWER;}

    public void setANSWER(String ANSWER) { this.ANSWER = ANSWER; }

    public String getQuesID() {
        return QuesID;
    }

    public void setQuesID(String quesID) {
        QuesID = quesID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getResponseType() {
        return ResponseType;
    }

    public void setResponseType(String responseType) {
        ResponseType = responseType;
    }

    public String getSelectionText() {
        return SelectionText;
    }

    public void setSelectionText(String selectionText) {
        SelectionText = selectionText;
    }

    public String getValuemin() {
        return Valuemin;
    }

    public void setValuemin(String valuemin) {
        Valuemin = valuemin;
    }

    public String getValueMax() {
        return ValueMax;
    }

    public void setValueMax(String valueMax) {
        ValueMax = valueMax;
    }

    public String getAnswer() {   return answer;   }

    public void setAnswer(String answer) {   this.answer = answer;   }

    public String getQuesCode() {    return QuesCode;    }

    public void setQuesCode(String quesCode) {    QuesCode = quesCode;    }
}
