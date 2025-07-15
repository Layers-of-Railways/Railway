// Utility file for a live template.

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

def doc = _editor.getDocument();
def project = _editor.getProject();

def psi_dm = PsiDocumentManager.getInstance(project);

def psi_file = psi_dm.getPsiFile(doc);

if (psi_file == null) {
    return "// TODO: init. Could not find PSI";
}

int offset = _editor.getCaretModel().getOffset();
def psi_element = psi_file.findElementAt(offset);

if (psi_element == null) {
    return "// TODO: init. Could not find PSI element";
}

def psi_cls = PsiTreeUtil.getParentOfType(psi_element, PsiClass.class);

def out = new StringBuilder();

psi_cls.accept(new JavaRecursiveElementVisitor() {
    public void visitField(PsiField field) {
        super.visitField(field);
        def ty = field.getType();
        if (ty.getArrayDimensions() == 0 && ty.getName() == "Affine") {
            out.append("${field.getName()} = prov.create(null);");
        } else if (ty.getArrayDimensions() == 1
                && ty.getComponentType().getName() == "Affine") {
            if (field.hasInitializer()) {
                out.append("prov.create(null, ${field.getName()});");
            } else {
                out.append("${field.getName()} = prov.create(null, 0);");
            }
        }
    }
});

return out.toString();
