var LED = document.getElementById("circle");
var button = document.getElementById("button");
var frequency_field = document.getElementById("freq_text");
var freq = 1.5;
var status = document.getElementById("status");
var delay = (1.0 / freq) * 1000;
var id = null;


function sleep(millis)
{
    var date = new Date();
    var curDate = null;
    do { curDate = new Date(); }
    while(curDate-date < millis);
}

function commitFrequency()
{
    freq = parseFloat(frequency_field.value);
    if(isNaN(freq))
    {
        status.innerHTML = "Invalid Frequency";
        status.innerHTML = "Blink Frequency";
    }

    delay = (1.0/freq) * 1000;
    setupLED();
}

function blinkLED()
{
    LED.style.backgroundColor = "red"; 
    setTimeout(function(){LED.style.backgroundColor = "#8b0000"; }, 200);
}


function setupLED()
{
    clearInterval(id);
    id = setInterval(blinkLED, delay);
}

button.onclick = commitFrequency;

setupLED();
