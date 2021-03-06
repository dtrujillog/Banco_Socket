/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * |
 *
 * @author Dtrujillo
 */
public class ConexionSQL extends javax.swing.JFrame implements Runnable {

    /**
     * Creates new form ConexionSQL
     */
    public ConexionSQL() {
        initComponents();
        Thread hilo = new Thread(this);
        hilo.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        campo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        campo.setColumns(20);
        campo.setRows(5);
        jScrollPane1.setViewportView(campo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConexionSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConexionSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConexionSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConexionSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ConexionSQL().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea campo;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {

        try {
            ServerSocket servidor = new ServerSocket(5050);
            while (true) {
                Socket misocket = servidor.accept();
                DataInputStream recibir_datos = new DataInputStream(misocket.getInputStream());
                String mensaje = recibir_datos.readUTF();
                campo.append(mensaje+"\n");

                //aqui va la consulta de la base de datos
                //String mensaje = "";
                PreparedStatement ps;
                ResultSet rs;
                ResultSetMetaData rsmd;
                int columnas;
                try {
                    Connection con = Conexion.getConexion();
                    ps = con.prepareStatement(mensaje);
                    rs = ps.executeQuery();

                    rsmd = rs.getMetaData();
                    columnas = rsmd.getColumnCount();
                    DataOutputStream enviar_datos = new DataOutputStream(misocket.getOutputStream());

                    String todo = new String();
                    while (rs.next()) {
                        String[] fila = new String[columnas];
                        for (int indice = 0; indice < columnas; indice++) {
                            fila[indice] = rs.getObject(indice + 1).toString();
                        }
                        todo += Arrays.toString(fila);
                    }
                    enviar_datos.writeUTF(todo);
                    campo.append(todo+"\n");

                } catch (HeadlessException | SQLException ex) {
                    DataOutputStream enviar_datos = new DataOutputStream(misocket.getOutputStream());
                    enviar_datos.writeUTF(ex.getMessage());
                      // enviar_datos.writeUTF("Transaccion Terminada");
                }

                //aqui termina la consulta de la base de datos
            }
        } catch (HeadlessException | IOException e) {
            System.out.println(e);
        }

    }
}
