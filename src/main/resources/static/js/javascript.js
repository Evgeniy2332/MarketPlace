$('.login-button').hover(
    function() {
        $(this).animate({ backgroundColor: '#000' }, 500);
    },
    function() {
        $(this).animate({ backgroundColor: '#333' }, 500);
    }
);


<a href="#" data-toggle="tooltip" data-placement="top" title="Это всплывающая подсказка">Нажмите здесь</a>

.button:hover {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
}

$('#slider').slider({
    min: 0,
    max: 100,
    value: 50,
    slide: function(event, ui) {
        $('#amount').val(ui.value);
    }
});