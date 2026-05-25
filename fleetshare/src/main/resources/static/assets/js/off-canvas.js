(function($) {
  'use strict';
  $(function() {
    // Restore state from localStorage on page load
    if (localStorage.getItem('mobileMenuState') === 'open') {
        $('.sidebar-offcanvas').addClass('active');
    }

    $('[data-toggle="offcanvas"]').on("click", function() {
      $('.sidebar-offcanvas').toggleClass('active');
      
      // Save the new state to localStorage
      if ($('.sidebar-offcanvas').hasClass('active')) {
          localStorage.setItem('mobileMenuState', 'open');
      } else {
          localStorage.setItem('mobileMenuState', 'closed');
      }
    });
  });
})(jQuery);