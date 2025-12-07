/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gallery_m.domain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordDirecto {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String passwordIngresada = "123";
        String hashActualEnBD = "$2a$10$N9qo8uLOickgx2ZMRZoMye/IFdJHqNq7b2PWMq7XHqKjJlqfXp0qO";
        
        System.out.println("==========================================");
        System.out.println("TEST 1: Validar hash actual");
        System.out.println("==========================================");
        System.out.println("Password: " + passwordIngresada);
        System.out.println("Hash en BD: " + hashActualEnBD);
        boolean validaActual = encoder.matches(passwordIngresada, hashActualEnBD);
        System.out.println("¿Valida?: " + validaActual);
        
        if (!validaActual) {
            System.out.println("\n❌ EL HASH ACTUAL NO VALIDA");
            System.out.println("\n==========================================");
            System.out.println("GENERANDO NUEVO HASH VÁLIDO");
            System.out.println("==========================================");
            
            String nuevoHash = encoder.encode(passwordIngresada);
            System.out.println("Nuevo hash generado:");
            System.out.println(nuevoHash);
            
            boolean validaNuevo = encoder.matches(passwordIngresada, nuevoHash);
            System.out.println("\n¿Este nuevo hash valida?: " + validaNuevo);
            
            System.out.println("\n==========================================");
            System.out.println("USA ESTE SQL PARA ACTUALIZAR:");
            System.out.println("==========================================");
            System.out.println("UPDATE usuario SET password = '" + nuevoHash + "' WHERE username IN ('juan', 'rebeca', 'pedro', 'admin');");
            System.out.println("==========================================");
        } else {
            System.out.println("\n✅ EL HASH ACTUAL ES VÁLIDO");
            System.out.println("El problema NO es el hash.");
        }
    }
}