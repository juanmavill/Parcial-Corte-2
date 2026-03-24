package co.edu.eci.proxy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin(origins = "*")
public class ProxyController {

    // las direcciones de los dos servidores se leen de variables de entorno
    // si no hay variable de entorno usa localhost por defecto
    private String instancia1 = System.getenv("MATH_SERVICE_1") != null ? System.getenv("MATH_SERVICE_1") : "http://localhost:8080";
    private String instancia2 = System.getenv("MATH_SERVICE_2") != null ? System.getenv("MATH_SERVICE_2") : "http://localhost:8081";

    @GetMapping("/proxy/catalan")
    public String catalan(@RequestParam(value = "value", defaultValue = "0") String value) {

        // primero intento con la instancia 1 (activa)
        if (estaVivo(instancia1)) {
            try {
                return llamarServicio(instancia1 + "/catalan?value=" + value);
            } catch (Exception e) {
                // si fallo intento con la 2
            }
        }

        // si la instancia 1 esta caida intento con la instancia 2 (pasiva)
        if (estaVivo(instancia2)) {
            try {
                return llamarServicio(instancia2 + "/catalan?value=" + value);
            } catch (Exception e) {
                // si tambien fallo retorno error
            }
        }

        return "{\"error\": \"los dos servicios estan caidos\"}";
    }

    // metodo para verificar si un servicio esta vivo
    private boolean estaVivo(String baseUrl) {
        try {
            URL url = new URL(baseUrl + "/health");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            int codigo = con.getResponseCode();
            return codigo == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // metodo para llamar un servicio GET y retornar la respuesta
    private String llamarServicio(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int codigo = con.getResponseCode();
        if (codigo == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String linea;
            StringBuffer respuesta = new StringBuffer();
            while ((linea = in.readLine()) != null) {
                respuesta.append(linea);
            }
            in.close();
            return respuesta.toString();
        } else {
            throw new IOException("el servicio respondio con codigo: " + codigo);
        }
    }

}
