Lista de Contatos - Aplicativo Android
Sobre o Projeto
Este é um aplicativo Android desenvolvido em Java que serve como uma lista de contatos digital. Ele permite que os usuários gerenciem suas informações de contato de forma eficiente e intuitiva em seus dispositivos móveis. O projeto foi construído com foco na funcionalidade principal de um CRUD (Criar, Ler, Atualizar, Deletar) de contatos, com uma interface de usuário aprimorada utilizando Material Design Components para uma experiência moderna.

Funcionalidades Principais
Autenticação de Usuário:

Tela de login simples com credenciais pré-definidas (usuário: admin, senha: 123) para acesso às funcionalidades principais.
Gerenciamento de Contatos:

Adicionar Contatos: Permite adicionar novos contatos com informações como nome, número de telefone e endereço de e-mail.
Visualizar Contatos: Exibe todos os contatos salvos em uma lista organizada e visualmente agradável, utilizando RecyclerView para performance e MaterialCardView para o design de cada item.
Alterar Contatos: Permite editar as informações de um contato existente.
Remover Contatos: Possibilita a exclusão de contatos da lista.
Persistência de Dados:

Os dados dos contatos são armazenados localmente no dispositivo usando um banco de dados SQLite, garantindo que as informações persistam mesmo após o fechamento do aplicativo.
Interface de Usuário (UI) e Experiência do Usuário (UX):

Interface modernizada com Material Design Components, incluindo:
MaterialToolbar para barras de título consistentes.
TextInputLayout para campos de entrada de texto aprimorados.
MaterialButton e FloatingActionButton (FAB) para ações claras.
MaterialCardView para exibir cada contato na lista.
Operações de banco de dados são executadas em segundo plano utilizando AsyncTask para manter a interface do usuário responsiva e evitar travamentos.
Para que Serve?
Este aplicativo serve como um exemplo prático de como construir um aplicativo Android com funcionalidades CRUD completas, autenticação básica, persistência de dados local com SQLite e uma interface de usuário moderna. É uma excelente base para:

Aprender os fundamentos do desenvolvimento Android com Java.
Entender como implementar interações com banco de dados SQLite.
Praticar a criação de interfaces de usuário responsivas e visualmente agradáveis com Material Design.
Compreender a importância de executar tarefas demoradas (como operações de I/O do banco de dados) em threads separadas da UI principal.
