   // Funzione per il reindirizzamento dopo un ritardo di 5 secondi
   function redirectAfterDelay() {
       setTimeout(function () {
           window.location.href = "/ChargingStation/Welcome/Logged";
       }, 5000); // 5000 millisecondi = 5 secondi
   }

   // Chiamare la funzione al caricamento della pagina
   window.onload = redirectAfterDelay;