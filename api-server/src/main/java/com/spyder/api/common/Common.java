package com.spyder.api.common;

public final class Common {

    private Common(){}

    public static class Util{

        public static void validateEmail(String email) throws Exception {
            if (!email.contains("@") || !email.contains(".")) {
                throw new Exception("Invalid email!");
            }
        }

    }

    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

}
