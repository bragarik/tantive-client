# tantive-client
[![Bitbucket issues](https://img.shields.io/bitbucket/issues-raw/bragarik/tantive-client?style=plastic)](https://bitbucket.org/bragarik/tantive-client/issues/)

## Descrição

Client TCP com persistência e logs de mensagens.
1. Mensagem de texto
2. Informações de um usuário
3. Solicitar data e hora atual
 
## Desafio

O objetivo deste desafio é desenvolver um servidor TCP para o recebimento de mensagens de um cliente TCP e armazenamento delas em banco de dados H2 (https://www.h2database.com/). Para o desenvolvimento deve ser utilizado sockets Java nativo ou o framework Apache MINA (https://mina.apache.org/). O servidor deve receber e tratar as mensagens descritas no item Protocolo deste documento e responder corretamente às requisições feitas pelo Cliente, criar o próprio Cliente para comunicar com o Servidor (Será um diferencial).

#### Requisitos
- Servidor TCP em Java
- Projeto deve usar Maven
- Projeto deve possuir testes unitários
- Servidor deve aceitar mais de 1 cliente ao mesmo tempo
- O projeto deve estar em um servidor Git (Bitbucket, Github, etc)
- Utilizar Hibernate com o banco de dados H2

---

**Instalação**
`git clone https://bragarik@bitbucket.org/bragarik/tantive-client.git`

**Execução**
Entre no diretório onde está o arquivo `tantive-client/target/tantive-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

Digite `$java -jar ./tantive-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

---

### Reference Documentation
* [The MINA 2.1.3 User Guide - Documentation](http://mina.apache.org/mina-project/documentation.html)
* [Hibernate 5.4 - Documentation](https://hibernate.org/orm/documentation/5.4/)
* [H2database Quickstart - Documentation](https://www.h2database.com/html/quickstart.html)

**Author**
<p><i><u>Ricardo Braga</u></i> atualmente trabalha no mercado de <b>Equipamentos eletromédicos</b> como <i>desenvolvedor de software embarcados</i>. Ele é graduado pela <b>FAJ - Faculdade de Jaguariúna</b> (@2012) em <b>Ciência da Computação</b> e atua no mercado corporativo como desenvolvedor desde 2010.
</p>

**Fale Comigo o/**
<li>bragarik@gmail.com</li>
<li>https://www.linkedin.com/in/ricardo-braga/<https://www.linkedin.com/in/ricardo-braga/></li>
____
No Copyright © 2020, [Ricardo Braga](https://bitbucket.org/bragarik/)