package clases;

import java.util.Date;

public class ComprobantePago {

    private int idComprobantePago;
    private Date fechaEmision;
    private String pago;

    public ComprobantePago(int idComprobantePago, Date fechaEmision, String pago) {
        this.idComprobantePago = idComprobantePago;
        this.fechaEmision = fechaEmision;
        this.pago = pago;
    }

    public void emitirComprobante() {
        // Implementación vacía
    }

    public void consultarComprobante() {
        // Implementación vacía
    }

    // Getters y setters
    public int getIdComprobantePago() {
        return idComprobantePago;
    }

    public void setIdComprobantePago(int idComprobantePago) {
        this.idComprobantePago = idComprobantePago;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }
}
