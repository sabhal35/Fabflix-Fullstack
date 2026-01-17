let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        let redirectUrl = resultDataJson["redirect"] || "MainPage";
        window.location.href = redirectUrl;
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult,
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Login request failed: " + textStatus + " " + errorThrown);
                $("#login_error_message").text("An error occurred. Please try again.");
            }
        }
    );
}

login_form.submit(submitLoginForm);