package pl.eurekin.mdb.client;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author greg.matoga@gmail.com
 */
public class PortForwarder {

    private Session session;

    public PortForwarder() {
        try {
            JSch jsch = new JSch();
                       jsch.addIdentity("mykey",
                               ("-----BEGIN RSA PRIVATE KEY-----\n" +
                            "Proc-Type: 4,ENCRYPTED\n" +
                            "DEK-Info: DES-EDE3-CBC,53FF475904EFC1DE\n" +
                            "\n" +
                            "xVrwlVJpTiFfnHJPHjFC6/G8g28tg7m5xf/BPXoYdZIPPk7gEuBdr0WSZNhPJZ1p\n" +
                            "ZJKiZVmG9mq5z2gj8KOT2q5ab44mFWyYgwgTGB8xcteUqAPmJ7MUinfhAEF61jpe\n" +
                            "OgZoRkU2uX/4rf3fn8Hr2QRQom0nHjjtcUfvgeBCrqAF+y/f269AA9iMr2Gy8dUD\n" +
                            "YJH0EL7V2nJi4UW4dK/SKwH+KZh2kS3qmxnEtrrH6Xg/uZjc84YTLbBTZYGWCjwC\n" +
                            "NJvNoFnRdvhJuzurf6Wx2bV61f5GLP49d3q3CBzrrjMb+LfCBiOykW4cgLYR+Ky8\n" +
                            "0anGDfWNoKLke1NnPE9i4ACLJuuzLVifb/CeVsQamCXxGEMJIXOIj3kQYPOWFyCj\n" +
                            "lC8WNR9gDQo/9N5rCfSMdUmFe3++IB0AIqamyMsIarAvMr/mYHRGMdT9mDUIseBy\n" +
                            "Au5rlhpKmDNSg+M40ztrptIUbIIzcguqoVyj1+C+dMy5SIRw5gy1MlfHE73w3JCY\n" +
                            "gpa/6UMaSJKHn8MV4YNBLjw6MYAHZBnGVznBHLLqAi/JsvxYAVdxTob57kR9BxHL\n" +
                            "zdnBbCvTs5lLP+PAEXVqOAQr0lPclrI0JqHwjMng8Grth2lpmPRe39b+Utk0Aq67\n" +
                            "UTbBxWqfkTxDQAHnmx2pdx6a7UEdhJFaSg8BY4LAIb+fTPmYW36CIxtgxKFvtGMZ\n" +
                            "8ixmosFmbXtMaZ2KT/rkf+nysi39JjPBRsPR9iGXBXx0e/hM+9UTnKlVqigWFDzh\n" +
                            "wjaPgX4IfTkLRVVVUdk3fjQgkTK35OhvrksM+ohgjFg=\n" +
                            "-----END RSA PRIVATE KEY-----\n").getBytes(),
                               null, "GIUDURDIUYVOICIYTVOUVOTCITRCOUGIUGOYU214".getBytes()
            );
            String host = "pacelibom-eurekin.rhcloud.com";
            String user = "51bdf9004382ec63640003de";
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            UserInfo ui = new MyUserInfo();
            session = jsch.getSession(user, host, 22);
            session.setUserInfo(ui);
            session.setConfig(config);


            int [] ports = {4447,5445 };


            session.connect();

            // ip and ports are requested with the command:
            // rhc-list-ports
            //
            // sample output:
            //
            // java -> 127.3.199.1:3528
//            java -> 127.3.199.1:4447
//            java -> 127.3.199.1:5445
//            java -> 127.3.199.1:5455
//            java -> 127.3.199.1:8080
//            java -> 127.3.199.1:9990
//            java -> 127.3.199.1:9999
//            postgresql -> 127.3.199.2:5432
            //
            // line =~ /\A\s*(\S+) -> #{HOST_AND_PORT}\z/
            // # about the host information, be it numeric or FQDN in IPv4 or IPv6.
            // HOST_AND_PORT = /(.+):(#{UP_TO_65535})\b/


            for(int aport: ports) {
                String myip = "127.3.199.1";
                Integer i = session.setPortForwardingL(aport, myip, aport);
                System.out.println("i = " + i);
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
        public String getPassword(){ return passwd; }
        public boolean promptYesNo(String str){
            Object[] options={ "yes", "no" };
            int foo= JOptionPane.showOptionDialog(null,
                    str,
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return foo==0;
        }

        String passwd;
        JTextField passwordField=(JTextField)new JPasswordField(20);

        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return true; }
        public boolean promptPassword(String message){
            Object[] ob={passwordField};
            int result=
                    JOptionPane.showConfirmDialog(null, ob, message,
                            JOptionPane.OK_CANCEL_OPTION);
            if(result==JOptionPane.OK_OPTION){
                passwd=passwordField.getText();
                return true;
            }
            else{ return false; }
        }
        public void showMessage(String message){
            JOptionPane.showMessageDialog(null, message);
        }
        final GridBagConstraints gbc =
                new GridBagConstraints(0,0,1,1,1,1,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(0,0,0,0),0,0);
        private Container panel;
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts=new JTextField[prompt.length];
            for(int i=0; i<prompt.length; i++){
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]),gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if(echo[i]){
                    texts[i]=new JTextField(20);
                }
                else{
                    texts[i]=new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if(JOptionPane.showConfirmDialog(null, panel,
                    destination+": "+name,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                    ==JOptionPane.OK_OPTION){
                String[] response=new String[prompt.length];
                for(int i=0; i<prompt.length; i++){
                    response[i]=texts[i].getText();
                }
                return response;
            }
            else{
                return null;  // cancel
            }
        }
    }

}
