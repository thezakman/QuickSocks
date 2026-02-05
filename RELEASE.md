# 🚀 Release Checklist - QuickSocks

## Preparando uma Nova Release

### 1. Atualizar Versão
Edite **APENAS 2 arquivos**:

#### ✏️ Constants.java
```java
// src/main/java/com/tzm/dirtysocks/Constants.java
public static final String VERSION = "1.0.1";  // ← MUDE AQUI
```

#### ✏️ pom.xml
```xml
<version>1.0.1</version>  <!-- ← MUDE AQUI -->
```

### 2. Atualizar CHANGELOG.md
```markdown
## [1.0.1] - 2026-02-XX

### Added
- Nova feature X

### Fixed
- Bug Y corrigido

### Changed
- Melhoria Z
```

### 3. Compilar
```bash
mvn clean package
```

### 4. Testar
- [ ] Carregar JAR no Burp Suite
- [ ] Criar perfil novo
- [ ] Editar perfil
- [ ] Deletar perfil
- [ ] Toggle ON/OFF
- [ ] Import/Export
- [ ] Testar IP
- [ ] Verificar logs no Burp

### 5. Commit e Tag
```bash
git add .
git commit -m "Release v1.0.1"
git tag -a v1.0.1 -m "Version 1.0.1"
git push origin main
git push origin v1.0.1
```

### 6. Criar Release no GitHub
1. Ir para: https://github.com/thezakman/QuickSocks/releases/new
2. Tag: `v1.0.1`
3. Title: `QuickSocks v1.0.1`
4. Descrição: Copiar do CHANGELOG.md
5. Anexar: `target/QuickSocks-1.0.1-jar-with-dependencies.jar`
6. Publicar ✅

---

## 📋 Pre-Release Checklist

Antes de cada release, verifique:

- [ ] Versão atualizada em `Constants.java`
- [ ] Versão atualizada em `pom.xml`
- [ ] CHANGELOG.md atualizado
- [ ] Todos os testes passando
- [ ] Sem erros de compilação
- [ ] JavaDoc sem warnings
- [ ] README atualizado (se necessário)
- [ ] Código revisado e testado

---

## 🏷 Semantic Versioning

Siga estas regras para numerar versões:

### MAJOR.MINOR.PATCH (Ex: 1.2.3)

- **MAJOR** (1.x.x): Mudanças incompatíveis na API
  - Exemplo: Remover funcionalidade, mudar estrutura de dados

- **MINOR** (x.2.x): Novas funcionalidades (backward compatible)
  - Exemplo: Adicionar nova feature, novo tipo de profile

- **PATCH** (x.x.3): Bug fixes (backward compatible)
  - Exemplo: Corrigir bug, melhorar performance

### Exemplos:
- `1.0.0` → `1.0.1`: Bug fix
- `1.0.1` → `1.1.0`: Nova feature
- `1.1.0` → `2.0.0`: Breaking change

---

## 📝 Tipos de Release Notes

### Para Bug Fix (1.0.0 → 1.0.1)
```markdown
## [1.0.1] - 2026-02-XX

### Fixed
- Corrigido erro ao importar profiles vazios
- Validação de porta agora aceita valores corretos
```

### Para Nova Feature (1.0.0 → 1.1.0)
```markdown
## [1.1.0] - 2026-02-XX

### Added
- Suporte para SOCKS4/SOCKS5 selecionável
- Grupos de profiles

### Fixed
- Bug X corrigido
```

### Para Breaking Change (1.0.0 → 2.0.0)
```markdown
## [2.0.0] - 2026-02-XX

### BREAKING CHANGES
- Formato de storage de profiles mudou
- Usuários precisam re-importar profiles antigos

### Added
- Nova arquitetura de profiles
```

---

## 🔄 Processo Completo de Release

```bash
# 1. Criar branch de release
git checkout -b release/v1.0.1

# 2. Atualizar versão (Constants.java + pom.xml)
# 3. Atualizar CHANGELOG.md

# 4. Compilar e testar
mvn clean package
# Testar JAR no Burp Suite

# 5. Commit
git add .
git commit -m "Bump version to 1.0.1"

# 6. Merge para main
git checkout main
git merge release/v1.0.1

# 7. Tag e push
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin main
git push origin v1.0.1

# 8. Criar release no GitHub
# https://github.com/thezakman/QuickSocks/releases/new

# 9. Deletar branch de release (opcional)
git branch -d release/v1.0.1
```

---

## 📦 Arquivos da Release

Certifique-se de incluir na release do GitHub:

- ✅ **JAR principal**: `QuickSocks-X.X.X-jar-with-dependencies.jar`
- ✅ **Release notes**: Copiadas do CHANGELOG.md
- ✅ **Tag correta**: vX.X.X
- ✅ **Descrição clara**: O que mudou nesta versão

---

## 🎯 Dicas

### ✅ DO's
- Sempre teste no Burp Suite antes de publicar
- Mantenha CHANGELOG.md atualizado
- Use Semantic Versioning
- Escreva release notes claras
- Tag sempre no formato `vX.X.X`

### ❌ DON'Ts
- Não pule etapas de teste
- Não esqueça de atualizar o CHANGELOG
- Não use números de versão inconsistentes
- Não faça release direto da branch de desenvolvimento
- Não esqueça de fazer push das tags!

---

## 🆘 Problemas Comuns

### "Tag já existe"
```bash
# Deletar tag local e remota
git tag -d v1.0.1
git push origin :refs/tags/v1.0.1
# Criar novamente
git tag -a v1.0.1 -m "Version 1.0.1"
git push origin v1.0.1
```

### "JAR não compila"
```bash
# Limpar completamente
mvn clean
rm -rf target/
mvn package
```

### "Esqueci de atualizar a versão"
```bash
# Desfazer último commit (mantém mudanças)
git reset --soft HEAD~1
# Atualizar versão
# Commit novamente
git commit -m "Bump version to X.X.X"
```

---

**Bom Release! 🚀**
