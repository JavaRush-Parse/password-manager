import { togglePasswordVisibility } from './modules/passwordEditForm.js';

window.togglePasswordVisibility = togglePasswordVisibility;

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function(event) {
            if(!(event instanceof InputEvent)){
                if (event.target.value === ""){
                    htmx.ajax('GET', '/vault-table-search', {target: '#vault-table-container', swap: 'outerHTML'});
                }
            }
        });
    }
});

document.body.addEventListener('htmx:afterSettle', function(event) {
    const searchInput = document.getElementById('searchInput');
    if (searchInput && event.detail.target.id === 'vault-table-container') {
        highlightSearchResults();
    }
});

function highlightSearchResults() {
    const searchInput = document.getElementById('searchInput');
    const searchText = searchInput ? searchInput.value.trim() : '';
    
    const tableContainer = document.getElementById('vault-table-container');
    if (!tableContainer || !searchText) return;
    
    const textNodes = getTextNodes(tableContainer);
    const escapedText = escapeRegex(searchText);
    
    textNodes.forEach(node => {
        const text = node.textContent;
        const regex = new RegExp(`(${escapedText})`, 'gi');
        
        if (regex.test(text)) {
            const fragment = document.createDocumentFragment();
            let lastIndex = 0;
            
            regex.lastIndex = 0;
            
            text.replace(regex, (match, p1, offset) => {
                if (offset > lastIndex) {
                    fragment.appendChild(document.createTextNode(text.substring(lastIndex, offset)));
                }
                
                const mark = document.createElement('mark');
                mark.className = 'search-highlight bg-yellow-200';
                mark.textContent = match;
                fragment.appendChild(mark);
                
                lastIndex = offset + match.length;
            });
            
            if (lastIndex < text.length) {
                fragment.appendChild(document.createTextNode(text.substring(lastIndex)));
            }
            
            node.parentNode.replaceChild(fragment, node);
        }
    });
}

function getTextNodes(element) {
    const textNodes = [];
    const walker = document.createTreeWalker(
        element,
        NodeFilter.SHOW_TEXT,
        {
            acceptNode: function(node) {
                if (node.parentElement.tagName === 'SCRIPT' || 
                    node.parentElement.tagName === 'STYLE' ||
                    !node.textContent.trim()) {
                    return NodeFilter.FILTER_REJECT;
                }
                return NodeFilter.FILTER_ACCEPT;
            }
        }
    );
    
    let node;
    while (node = walker.nextNode()) {
        textNodes.push(node);
    }
    
    return textNodes;
}

// Escapes regex special characters
function escapeRegex(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

// Expose globally
window.highlightSearchResults = highlightSearchResults;

