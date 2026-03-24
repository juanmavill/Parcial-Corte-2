package co.edu.eci.mathservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.math.BigInteger;

@RestController
@CrossOrigin(origins = "*")
public class MathController {

    // este metodo calcula los numeros de catalan desde C0 hasta Cn
    @GetMapping("/catalan")
    public String catalan(@RequestParam(value = "value", defaultValue = "0") int n) {

        if (n < 0) {
            return "{\"error\": \"el numero debe ser mayor o igual a 0\"}";
        }

        // arreglo para guardar los valores
        BigInteger[] c = new BigInteger[n + 1];
        c[0] = BigInteger.ONE;

        // calculo con programacion dinamica
        for (int k = 1; k <= n; k++) {
            c[k] = BigInteger.ZERO;
            for (int i = 0; i < k; i++) {
                c[k] = c[k].add(c[i].multiply(c[k - 1 - i]));
            }
        }

        // armo la lista como string
        String lista = "";
        for (int k = 0; k <= n; k++) {
            if (k == 0) {
                lista = c[k].toString();
            } else {
                lista = lista + ", " + c[k].toString();
            }
        }

        String resultado = "{";
        resultado = resultado + "\"operation\": \"Secuencia de Catalan\",";
        resultado = resultado + "\"input\": " + n + ",";
        resultado = resultado + "\"output\": \"" + lista + "\"";
        resultado = resultado + "}";

        return resultado;
    }

    // para saber si el servicio esta vivo
    @GetMapping("/health")
    public String health() {
        return "UP";
    }

}
