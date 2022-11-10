## Mocks em Java

### O que é um mock?

É uma classe que simula os comportamentos de outra classe.

## Criando Mocks

Podem ser criados a partir do segunte código `Mockito.mock(Classe.class);`

Para automatizar a criação de mocks, existem também a anotação `@Mock` que server para indicar que o atributo é um mock, e os mocks com o seguinte código: `MockitoAnnotations.initMocks(this);`

```java
@Mock
private PagamentoDao dao;

@Mock
private Clock clock;

@Captor
private ArgumentCaptor<Pagamento> captor;

@BeforeEach
void setUp() {
	MockitoAnnotations.initMocks(this);
  gerador = new GeradorDePagamento(dao, clock);
}
```

## Manipulando Mocks - Comportamentos

Para definir o comportamento do mock é necessário o seguinte comando: `Mockito.when(funcao.chamar()).thenReturn(valor)`

O método `verify()` serve para informar se um determindo método foi executado, já o `verifyNoInterations()` serve para o contrário, verificar se nenhum método do mock foi acionado. Para usar-los deve seguir os seguintes comandos:

`Mockito.verify(mock).funcaoDoMock(parametro);`

`Mockito.verifyNoInterations(mock);`

## Manipulando mocks - lançando exceções

```java
doThrow(new RuntimeException("Erro ao salvar")).when(dao).salvar(any());

try {
	service.finalizarLeiloesExpirados();
  fail();
} catch (RuntimeException e) {
  verifyNoInteractions(enviadorDeEmails);
}
```

## Capturando Objetos

Para captar componentes internos da classe mockada. o mockito utiliza-se do ArgumentCaptor.

```java
@Captor
private ArgumentCaptor<Pagamento> captor;
```

`captor.capture()` → Método que captura o objeto passado por parâmetro da função a ser testada

`captor.getValue()` →pega o valor do objeto capturado
