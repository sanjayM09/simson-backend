//package com.example.kkBazar.entity;
//
//import com.google.gson.Gson;
//
//public class FirebaseServiceAccount {
//    private String type;
//    private String project_id;
//    private String private_key_id;
//    private String private_key;
//    private String client_email;
//    private String client_id;
//    private String auth_uri;
//    private String token_uri;
//    private String auth_provider_x509_cert_url;
//    private String client_x509_cert_url;
//    private String universe_domain;
//
//    // Getters and setters
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getProject_id() {
//        return project_id;
//    }
//
//    public void setProject_id(String project_id) {
//        this.project_id = project_id;
//    }
//
//    public String getPrivate_key_id() {
//        return private_key_id;
//    }
//
//    public void setPrivate_key_id(String private_key_id) {
//        this.private_key_id = private_key_id;
//    }
//
//    public String getPrivate_key() {
//        return private_key;
//    }
//
//    public void setPrivate_key(String private_key) {
//        this.private_key = private_key;
//    }
//
//    public String getClient_email() {
//        return client_email;
//    }
//
//    public void setClient_email(String client_email) {
//        this.client_email = client_email;
//    }
//
//    public String getClient_id() {
//        return client_id;
//    }
//
//    public void setClient_id(String client_id) {
//        this.client_id = client_id;
//    }
//
//    public String getAuth_uri() {
//        return auth_uri;
//    }
//
//    public void setAuth_uri(String auth_uri) {
//        this.auth_uri = auth_uri;
//    }
//
//    public String getToken_uri() {
//        return token_uri;
//    }
//
//    public void setToken_uri(String token_uri) {
//        this.token_uri = token_uri;
//    }
//
//    public String getAuth_provider_x509_cert_url() {
//        return auth_provider_x509_cert_url;
//    }
//
//    public void setAuth_provider_x509_cert_url(String auth_provider_x509_cert_url) {
//        this.auth_provider_x509_cert_url = auth_provider_x509_cert_url;
//    }
//
//    public String getClient_x509_cert_url() {
//        return client_x509_cert_url;
//    }
//
//    public void setClient_x509_cert_url(String client_x509_cert_url) {
//        this.client_x509_cert_url = client_x509_cert_url;
//    }
//
//    public String getUniverse_domain() {
//        return universe_domain;
//    }
//
//    public void setUniverse_domain(String universe_domain) {
//        this.universe_domain = universe_domain;
//    }
//
//    // Method to parse JSON string into object
//    public static FirebaseServiceAccount fromJson(String json) {
//        return new Gson().fromJson(json, FirebaseServiceAccount.class);
//    }
//
//    // Method to convert object to JSON string
//    public String toJson() {
//        return new Gson().toJson(this);
//    }
//
//    public static void main(String[] args) {
//        String json = "{\n" +
//                "  \"type\": \"service_account\",\n" +
//                "  \"project_id\": \"kk-bazar\",\n" +
//                "  \"private_key_id\": \"0c28bddac36797e3f54a3bf623d2604dc5a65792\",\n" +
//                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDMXPOqiQ6LuX7q\\nl8tHhcJ/Qk1tLOdJU6xEHoS7UJ8KEIaOc7cdo33K+o1x13xrlmrJ1Thv1sLX11Hi\\nkNR93Obc044urW8bVLIqAevcqx/o438ehXXDbyQOMVefAHANdvONNMP5x8eAkIrQ\\nsWFa4k+0xcLCyIAdezdf/fP93npEh2X5kQRFypCV0FHxyZH1WGXN2+MmlU2WPbDf\\nscpbOgtLPgUlbnjLWgo2JiEEhMIkNSpmNbhynuYdjprt6IjjS2UdxGHSyya5mO12\\n6rfHxrTDvx4+ywuZSKnj/NptFl1xkFIRxNJsldgvF/SMiPQscDUaRdWV4BUSnKmW\\nuRNmfMhVAgMBAAECggEAJBFRperriMhzuFaS92wJkCk0/gw7Q6tDT4BtX5vSfdW9\\n0b1ld5Mnu9+tgAaOfHR/I2EAxWhLWgmz938lKZOHoTirMtzEK7gue5B8PqVrC9C+\\n2jWmu5fNbr3Rn4vVuuKgCG9kGRzjvnKENWHg80fW1mpCvdriFZHuC4CIA+m0fYxx\\nNv8iY+shxcZjD0iSMKqFdTMhlv22sShF0/749YVscLTOSAOUf6fTa0OxcMYNZ7fx\\ns/WQDqslqmfLLVZZKNn7w7jeYuwbzXATYUFE6xBmmITjN6Owfvk4QkSl7Kruq5U5\\nffGJ59iulTFHFpvxkzSMpVc05dI1tR5aIOJwNJ19/wKBgQDpr5xPlLxL+ZKxi0CS\\n+dIZLeG0NfkYgade1EVmFMwgtHRZh0BmtbO0MnqUaS/2n3dbN9+4gVtgGH4oQQSk\\nWOvwPlcQcHr0gHtISkmG6XV1Nv5AXllLyIkwNHPCzsXnUh3yIoraHAxlCZDgCBO/\\nJ6TGnytrXC9xuEBUkYB2L16YXwKBgQDf4I0mCG+VApRkIQB1dzWk8825GglGLlFE\\nJnllUMScmdYv0lP+vDbzWuHb2rKlQX53e0k9Y6cuNNbHZaqAkqtnKCnuk48dcHY2\\nx315xEsJU3Z5DL7QH64bYLNDH+qkhir2GjU0JG9pejBj90V+33QnYaR/KZYAI0gb\\nto81AF4rywKBgG7qPSTAkcb1xByk3ZdlH8V3s4MXgw2QrGIkGnRjNqfnPronqfeW\\nEp9avjem3W8PLVWMZZFrRHWN6oMsqQlgc0vqKuTQnmtieCFNsSxprqhd27utjHCd\\n1A+fNbqm7ePnY/NDVehEG9Xw/mR3MPgv1tO3RKKGdLZjH5WEIhb2UM29AoGAekw3\\nrxQiqFsNZxlkH8csZdzNgBWKBW2d9UWzQTZ7ATVEfUE/o0N8HTqDcU+qEX6SsICH\\nxOJgXGx++9Q87RmySpr1NSAkqSdbXJdjoxYpC4ItkPj2b8kumkQcCUoxKwkkBObS\\nM5bHye3261UUALfewvijHNhB2fdsF7+FwaVezDsCgYAeP2HX/wwZUnvkc/X5/aIG\\nfdPuRhEqU/PPcZaB6GPxKYFsHt9OF3iDB/TXiGCbm4769e7C5QSQoOXPkgzjwFhp\\n3rq63Gj9bqplTCl+ATHZBbLQ9MLF6WW0GOs7A8cyQqQkEQihXOHbOambQZ4AgpmR\\nJCNFe0TuYMyETl+x6gsSIA==\\n-----END PRIVATE KEY-----\\n\",\n" +
//                "  \"client_email\": \"firebase-adminsdk-n9ml6@kk-bazar.iam.gserviceaccount.com\",\n" +
//                "  \"client_id\": \"109371797803504163166\",\n" +
//                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
//                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
//                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
//                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-n9ml6%40kk-bazar.iam.gserviceaccount.com\",\n" +
//                "  \"universe_domain\": \"googleapis.com\"\n" +
//                "}";
//        
//        FirebaseServiceAccount serviceAccount = FirebaseServiceAccount.fromJson(json);
//        
//    }
//}
